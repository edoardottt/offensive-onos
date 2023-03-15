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

# -------- data creation --------


def generate_cap_attacks():
    result = []
    temp = []

    # getHost and/or forward (even 0)
    comb = []
    for i in range(6):
        comb += [p for p in itertools.product(apps["org.onosproject.fwd"], repeat=i)]
    for elem in comb:
        if len(elem) > 0:
            to_add = "org.onosproject.fwd" + "org.onosproject.fwd".join(elem)
            temp += [to_add]

    # appendLocation and/or RemoveLocation (at least 1)
    comb = []
    temp2 = []
    for i in range(1, 5):
        comb += [
            p
            for p in itertools.product(
                apps["org.edoardottt.malhosttracking.app"], repeat=i
            )
        ]
    count = 0
    for lista in comb:
        for elem in temp:
            to_add = (
                "org.edoardottt.malhosttracking.app"
                + "org.edoardottt.malhosttracking.app".join(lista)
            )
            temp2 += [to_add]

    # getHost and/or forward (at least 1 forward)
    comb = []
    for i in range(1, 6):
        comb += [p for p in itertools.product(apps["org.onosproject.fwd"], repeat=i)]
    for lista in comb:
        for elem in temp2:
            if not (len(list(set(lista))) == 1 and list(set(lista))[0] == "getHost"):
                result += [
                    elem + "org.onosproject.fwd" + "org.onosproject.fwd".join(lista)
                ]

    for i in range(len(result)):
        result[i] = result[i] + tab + str(1)

    print("[ + ] Generated {} cap attacks!".format(len(result)))

    return result


def generate_not_cap_attacks():
    result = []

    # getHost and/or forward (even 0)
    comb = []
    for i in range(16):
        comb += [p for p in itertools.product(apps["org.onosproject.fwd"], repeat=i)]
    for elem in comb:
        if len(elem) > 0:
            to_add = "org.onosproject.fwd" + "org.onosproject.fwd".join(elem)
            result += [to_add]

    # getHost and/or forward (even 0)
    comb = []
    for i in range(16):
        comb += [
            p
            for p in itertools.product(
                apps["org.edoardottt.malhosttracking.app"], repeat=i
            )
        ]
    for elem in comb:
        if len(elem) > 0:
            to_add = (
                "org.edoardottt.malhosttracking.app"
                + "org.edoardottt.malhosttracking.app".join(elem)
            )
            result += [to_add]

    for i in range(len(result)):
        result[i] = result[i] + tab + str(0)

    print("[ + ] Generated {} not cap attacks!".format(len(result)))

    return result


# -------- create datasets --------


def create_data_folder():
    exist = os.path.exists(directory)
    if not exist:
        os.makedirs(directory)
        print("[ + ] Created output folder!")
        return
    print("[ + ] Output folder exists!")


def create_datasets(cap_attacks, not_cap_attacks, count):
    entries = cap_attacks + not_cap_attacks
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
            f.write(i + "\n")


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

    cap_attacks = generate_cap_attacks()
    not_cap_attacks = generate_not_cap_attacks()

    listA, listB = create_datasets(cap_attacks, not_cap_attacks, 90)

    mlA, mlB = trasform(listA[:], listB[:])

    print("[ + ] Training files contain {} entries!".format(str(len(listA))))
    print("[ + ] Test files contain {} entries!".format(str(len(listB))))

    # write_file(training_out_file, listA)
    # write_file(test_out_file, listB)
    write_file(training_out_file_ml, mlA)
    write_file(test_out_file_ml, mlB)
