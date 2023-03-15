#!/usr/bin/python3

# -------- imports --------
import os
import itertools
import random

# -------- global variables --------

directory = "/home/edoardottt/cybersecurity/thesis/onos-ml/data/"
training_out_file = "training-data.txt"
test_out_file = "test-data.txt"
training_out_file_ml = "training-data-ml.txt"
test_out_file_ml = "test-data-ml.txt"
space = " "
tab = "	"

apps = {
    "org.onosproject.fwd": ["getHost", "forward"],
    "org.edoardottt.malhosttracking.app": ["appendLocation", "removeLocation"],
}

app_name = {"org.onosproject.fwd": 1, "org.edoardottt.malhosttracking.app": 2}
action_name = {"getHost": 3, "forward": 4, "appendLocation": 5, "removeLocation": 6}

items = {
    "org.onosproject.fwd getHost": "1",
    "org.onosproject.fwd forward": "2",
    "org.edoardottt.malhosttracking.app appendLocation": "3",
    "org.edoardottt.malhosttracking.app removeLocation": "4",
}

items_fwd = {
    "org.onosproject.fwd getHost": "1",
    "org.onosproject.fwd forward": "2",
}

items_mal = {
    "org.edoardottt.malhosttracking.app appendLocation": "3",
    "org.edoardottt.malhosttracking.app removeLocation": "4",
}

fwds = ["1", "2"]
mals = ["3", "4"]

# -------- data creation --------


def is_attack(stringa):
    if "3" in stringa:
        index3 = stringa.index("3")
        if "2" in stringa and stringa.index("2", index3, len(stringa) - 1):
            return True

    if "4" in stringa:
        index4 = stringa.index("4")
        if "2" in stringa and stringa.index("2", index4, len(stringa) - 1):
            return True

    return False


def generate_cap(limit):
    result = [i for i in range(limit + 1)]

    temp = []
    temp += [p for p in itertools.product(items.values(), repeat=10)]
    for i in range(len(temp)):
        temp[i] += temp[i] + temp[i] + temp[i] + temp[i]
        if is_attack(temp[i]):
            temp[i] += (tab + "1",)
        else:
            temp[i] += (tab + "0",)
    for i in range(len(temp)):
        result[i] = temp[i]
        if i >= limit:
            break

    return result


def add_zeros():
    result = []

    temp = []
    temp += [p for p in itertools.product(items_fwd.values(), repeat=19)]
    for i in range(len(temp)):
        temp[i] += temp[i] + tuple(random.choices(fwds, k=12)) + (tab + "0",)

    temp2 = []
    temp2 += [p for p in itertools.product(items_mal.values(), repeat=19)]
    for i in range(len(temp)):
        temp2[i] += temp2[i] + tuple(random.choices(mals, k=12)) + (tab + "0",)

    for i in range(len(temp)):
        result.append(temp[i])

    for i in range(len(temp2)):
        result.append(temp2[i])

    return result


# -------- create datasets --------


def create_data_folder():
    exist = os.path.exists(directory)
    if not exist:
        os.makedirs(directory)
        print("[ + ] Created output folder!")
        return
    print("[ + ] Output folder exists!")


def create_datasets(entries, count):
    random.shuffle(entries)

    resultA = []
    resultB = []

    for i in range(int(len(entries) / 100 * count)):
        resultA.append(entries[i])

    for i in range(int(len(entries) / 100 * count), len(entries)):
        resultB.append(entries[i])

    return (resultA, resultB)


def write_file(file, input):
    with open(directory + file, "w+") as f:
        for i in input:
            f.write("".join(i) + "\n")


# -------- ML-friendly data transformation --------


def trasform(listA, listB):
    resultA = []
    resultB = []
    for strelem in listA:
        for elem in app_name:
            strelem = strelem.replace(elem, str(app_name[elem]))
        for elem in action_name:
            strelem = strelem.replace(elem, str(action_name[elem]))
        resultA.append(strelem)

    for strelem in listB:
        for elem in app_name:
            strelem = strelem.replace(elem, str(app_name[elem]))
        for elem in action_name:
            strelem = strelem.replace(elem, str(action_name[elem]))
        resultB.append(strelem)

    return (resultA, resultB)


# -------- main --------

if __name__ == "__main__":
    create_data_folder()

    mid_entries = generate_cap(1000000)

    entries = mid_entries + add_zeros()

    print("[ + ] Generated {} items!".format(len(entries)))

    listA, listB = create_datasets(entries, 90)

    print("[ + ] Training files contain {} entries!".format(str(len(listA))))
    print("[ + ] Test files contain {} entries!".format(str(len(listB))))

    # write_file(training_out_file, listA)
    # write_file(test_out_file, listB)
    write_file(training_out_file_ml, listA)
    write_file(test_out_file_ml, listB)
