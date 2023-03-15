#!/usr/bin/env python3
from pathlib import Path
from typing import Iterable, Tuple

import matplotlib.pyplot as plt
import numpy as np
import tensorflow.keras as keras


def read_dataset() -> Iterable[Tuple[str, bool]]:
    path = Path("gistfile1.txt")
    with path.open("r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            parts = line.split("\t", maxsplit=1)
            yield parts[0], parts[1] == "1"


dataset = list(set(read_dataset()))
max_len = max(len(x) for x, _ in dataset)

# Shuffle the dataset

np.random.shuffle(dataset)

# Pad the dataset with spaces


def left_pad_with(s: str, pad: str, length: int) -> str:
    assert len(pad) == 1
    return pad * (length - len(s)) + s


padded = [(left_pad_with(x, "0", max_len), y) for x, y in dataset]

alphabet = list(sorted(set("".join(x for x, _ in padded))))


def one_hot_encode(s: str) -> np.ndarray:
    eye = np.eye(len(alphabet), dtype=np.float32)
    enc = np.array([eye[alphabet.index(c)] for c in s])
    return enc.flatten()


X = np.array([one_hot_encode(x) for x, _ in padded])
y = np.array([y for _, y in padded])
labels = np.array([y for _, y in padded])


class ResidualDense(keras.layers.Layer):
    def __init__(self, units: int, activation: str, **kwargs):
        super().__init__(**kwargs)
        self.units = units
        self.activation = activation

    def build(self, input_shape: Tuple[int, ...]) -> None:
        self.dense = keras.layers.Dense(self.units, activation=self.activation)
        self.res = keras.layers.Dense(self.units, activation="linear")

    def call(self, inputs: keras.layers.Layer) -> keras.layers.Layer:
        return self.res(inputs) + self.dense(inputs)


encoder = keras.Sequential(
    [
        keras.layers.InputLayer(input_shape=(max_len * len(alphabet),)),
        ResidualDense(64, "relu"),
        ResidualDense(32, "relu"),
        ResidualDense(16, "relu"),
        ResidualDense(8, "relu"),
        ResidualDense(2, "relu"),
    ]
)

decoder = keras.Sequential(
    [
        ResidualDense(8, "relu"),
        ResidualDense(16, "relu"),
        ResidualDense(32, "relu"),
        ResidualDense(64, "relu"),
        ResidualDense(max_len * len(alphabet), "relu"),
    ]
)

autoencoder = keras.Sequential([encoder, decoder])


autoencoder.compile(
    optimizer="adam",
    loss="mse",
    metrics=["mae", "mse"],
)


autoencoder.fit(X, X, epochs=128, batch_size=len(X) // 64, shuffle=True)

# Project the data into the 2D space

encoded = encoder.predict(X)

# Plot the data

plt.scatter(encoded[:, 0], encoded[:, 1], c=labels, s=3, cmap="jet", alpha=0.5)
plt.show()