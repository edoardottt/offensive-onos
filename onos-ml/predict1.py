#!/usr/bin/python3

# -------- imports --------

from datetime import datetime
import sys
import matplotlib.pyplot as plt
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix

# -------- global variables --------

directory = "/home/edoardottt/cybersecurity/thesis/onos-ml/data/"
training_out_file = "training-data.txt"
test_out_file = "test-data.txt"
training_out_file_ml = "training-data-ml.txt"
test_out_file_ml = "test-data-ml.txt"


# -------- training --------


def read_file(filename, limit):
    x = np.zeros((0, 50))
    y = np.zeros((0, 1))
    count = 0
    with open(directory + filename, "r") as f:
        lines = f.readlines()
        for line in lines:
            count += 1
            first_input = [int(x) for x in line.split("\t")[0]]
            first_input_np = np.array(first_input)
            x = np.append(x, [first_input_np], axis=0)
            second_input = [int(line.split("\t")[1])]
            second_input_np = np.array(second_input)
            y = np.append(y, [second_input_np], axis=None)
            print("Read {}/{} lines".format(count, limit), flush=True, end="\r")
            sys.stdout.flush()
            if count >= limit:
                break

    print("", flush=True, end="\n")

    return x, y


def plt_cm(cm):
    fig, ax = plt.subplots(figsize=(8, 8))
    ax.imshow(cm)
    ax.grid(False)
    ax.xaxis.set(ticks=(0, 1), ticklabels=("Predicted 0s", "Predicted 1s"))
    ax.yaxis.set(ticks=(0, 1), ticklabels=("Actual 0s", "Actual 1s"))
    ax.set_ylim(1.5, -0.5)
    for i in range(2):
        for j in range(2):
            ax.text(j, i, cm[i, j], ha="center", va="center", color="red")
    plt.show()


# -------- main --------

if __name__ == "__main__":
    # start time
    start_time = datetime.now().strftime("%H:%M:%S")
    # convert time string to datetime
    t1 = datetime.strptime(str(start_time), "%H:%M:%S")
    print("Start time:", t1.time())

    x, y = read_file(training_out_file_ml, 200000)

    model = LogisticRegression(solver="newton-cg", random_state=0)

    model.fit(x, y)

    print("Model intercept: " + str(model.intercept_))

    print("Model coef: " + str(model.coef_))

    print(" ----------- Train data -----------")

    print("Model score: " + str(model.score(x, y)))

    print("Model confusion matrix: ")
    cm = confusion_matrix(y, model.predict(x))
    print(cm)

    # plt_cm(cm)

    print(" ----------- Test data -----------")

    x2, y2 = read_file(test_out_file_ml, 40000)

    print("Model score: " + str(model.score(x2, y2)))

    print("Model confusion matrix: ")
    cm = confusion_matrix(y2, model.predict(x2))
    print(cm)

    end_time = datetime.now().strftime("%H:%M:%S")
    t2 = datetime.strptime(str(end_time), "%H:%M:%S")
    print("End time:", t2.time())

    # get difference
    delta = t2 - t1

    # time difference in seconds
    print(f"Time difference is {delta.total_seconds()} seconds")
