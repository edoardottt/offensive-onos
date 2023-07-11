"""
Generate logs for CAP attacks detection simulation

https://github.com/edoardottt/offensive-onos
"""

# ----------- import -----------

import random
import sys


# ----------- global variables -----------

apps = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm']
stores = ['n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']
new_app = random.choice(apps)


def gen_apis():
    """
    This function generates the API dictionary.
    """
    result = {}
    i = 0
    for store in stores:
        i+=1
        result[i] = ['w', store]
        i+=1
        result[i] = ['r', store]
    return result

apis = gen_apis()
accessible_apis_n = 3   # number of APIs a single app can access

ms_start = 0                # start time (in milliseconds)
ms_end = 10000000           # end time (in milliseconds)
cap_interval = 1000         # time between a potential CAP attack and another (in ms)
api_interval = (500, 1000)    # time between an API call and another (in ms)
log_file = "test.log"
summary_file = "test-info.txt"
cap_created_file = "generated_cap.txt"
logs = []
cap_vectors = 3
cap_lengths = [3, 5]

def pick_target_apps(k):
    """
    Return a set of apps different from the app under test.
    The size of the set is defined by the inpu value.
    """
    result = []
    if k >= len(apps):
        print("Targets apps size should be < len(apps).")
        sys.exit()
    while len(result) < k:
        tmp_app = random.choice(apps)
        if tmp_app != new_app:
            result += [tmp_app]
    return result

targets_apps = pick_target_apps(3)


# ----------- functions -----------

def gen_legitimate_logs():
    """
    This function generates random logs for legitimate apps.
    """
    count = 0
    for app in apps:
        if app != new_app:
            ts_app = random.randint(0, api_interval[1])
            accessible_apis = random.choices(list(apis.keys()), k = accessible_apis_n)
            while ts_app <= ms_end:
                log_elements = [str(ts_app+random.randint(api_interval[0], api_interval[1])), 
                                app, 
                                str(random.choice(accessible_apis)), 
                                "params"
                                ]
                logs.append(log_elements)
                ts_app = ts_app + api_interval[1]
                count += 1
                sys.stdout.flush()
                print("Generated {} logs from legitimate apps!".format(count), flush=True, end="\r")
    print()
    return count


def gen_cap_logs(p = 50):
    """
    This function generates CAP attack logs based on the
    probability value passed as input.
    """
    count = 0
    ts_app = random.randint(0, cap_interval)
    accessible_ds = random.choices(stores, k = 2)
    available_cap_vectors = gen_cap_vectors(accessible_ds)
    while ts_app <= ms_end:
        if random.randint(0, 100) < p:
            # cap
            cap_elements = build_cap_logs(ts_app, random.choice(available_cap_vectors))
            for log in cap_elements:
                logs.append(log)
            count += 1
            sys.stdout.flush()
            print("Generated {} CAP attacks!".format(count), flush=True, end="\r")
        else:
            # no cap (random API call)
            api = find_api('w', random.choice(accessible_ds))
            log_elements = [str(ts_app+random.randint(api_interval[0], api_interval[1])), 
                            new_app, 
                            str(api), 
                            "params"]
            logs.append(log_elements)

        ts_app = ts_app + cap_interval
    print()
    return count


def gen_cap_sequence(target, accessible_ds, length):
    """
    Generate a CAP attack API sequence.
    Takes as input:
        - a target application
        - a list of data stores accessible from new_app
        - the length of desired CAP attack  
    """
    result = []
    taken_ds = []
    taken_apps = [new_app, target]
    length_cap = 0
    # add first write to random accessible data store
    first_ds = random.choice(accessible_ds)
    first_api = find_api('w', first_ds)
    taken_ds += [first_ds]
    result.append([new_app, str(first_api)])
    length_cap += 1
    while length_cap < length:
        if length_cap == length - 2: # add read from target
            chosen_ds = apis[int(result[len(result)-1][1])][1]
            api_call = find_api('r', chosen_ds)
            taken_ds += [chosen_ds]
            result.append([target, str(api_call)])
        else:
            # if last call is a read, then write to random available data store
            if apis[int(result[len(result)-1][1])][0] == 'r':
                app = result[len(result)-1][0]
                available_ds = [x for x in stores if x not in taken_ds]
                chosen_ds = random.choice(available_ds)
                api_call = find_api('w', chosen_ds)
                taken_ds += [chosen_ds]
            else:
                available_apps = [x for x in apps if x not in taken_apps]
                app = random.choice(available_apps)
                chosen_ds = apis[int(result[len(result)-1][1])][1]
                api_call = find_api('r', chosen_ds)
            result.append([app, str(api_call)])
        
        length_cap += 1

    return result


def gen_cap_vectors(accessible_ds):
    """
    Generate n random CAP vectors exploitable 
    for the app under test.
    """
    result = []
    for i in range(cap_vectors):
        result += [gen_cap_sequence(random.choice(targets_apps), accessible_ds, random.choice(cap_lengths))]
    return result


def random_api_call(store):
    """
    Return a random API given a data store (can be both r or w). 
    """
    available = []
    for api in apis.keys():
        if apis[api][1] == store:
            available += [api]
    return random.choice(available)


def find_api(action, store):
    """
    Return an API given an action (r/w) and a data store. 
    """
    for api in apis.keys():
        if apis[api][0] == action and apis[api][1] == store:
            return api
    return None


def build_cap_logs(ts_app, cap_sequence):
    """
    Return a sequence of logs from a CAP attack API sequence.
    """
    delta = cap_interval // len(cap_sequence)
    result = []
    drift = 0
    apis = []
    for elem in cap_sequence:
        elem_cap = [str(ts_app + drift), str(elem[0]), str(elem[1]), "params"]
        result.append(elem_cap)
        drift += delta
        apis += [(str(elem[0]), str(elem[1]))]
    cap_apis_created = api_to_cap_gadgets(apis)
    with open(cap_created_file, "a+") as f:
        f.write("".join(cap_apis_created) + "\n")
    return result


def api_to_cap_gadgets(api_pairs):
    """
    This function translates a sequence of pair 
    (apps and their associated APIs) to CAP gadget sequences.
    e.g.
    [(b,9), (a,10), (a,1)] ---> ['b', 'r', 'a', 'n']
    """
    result = []
    for i in range(0, len(api_pairs), 2):
        result += [api_pairs[i][0]]
        result += [apis[int(api_pairs[i][1])][1]]

    return result


def create_log_file():
    """
    This function dumps the logs in the log file.
    """
    ts_logs = map(lambda x: [int(x[0]), x[1], x[2], x[3]], logs)
    sorted_logs = sorted(ts_logs, key=lambda x: x[0])
    with open(log_file, "w+") as f:
        for log in sorted_logs:
            log_string = " ".join([str(x) for x in log])
            f.write(log_string + "\n")
    print("Generated {} logs!".format(len(sorted_logs)))
    return len(sorted_logs)


def print_apis():
    """
    This function prints the APIs as a dictionary.
    """
    print("{", end=" ")
    for x in apis:
        print("\t", '"' + str(x) + '"', ":", apis[x], ",")
    print("}")


def generate_test_summary(prob, legitimate_logs, caps, logs):
    with open(summary_file, "w+") as f:
        f.write("Apps: {}\n".format(apps))
        f.write("Stores: {}\n".format(stores))
        f.write("App under test: {}\n".format(new_app))
        f.write("APIs: {}\n".format(apis))
        f.write("CAP attack probability: {}%.\n".format(prob))
        f.write("CAP attack lengths: {}.\n".format(cap_lengths))
        f.write("CAP attack interval: {}.\n".format(cap_interval))
        f.write("Generated {} logs from legitimate apps.\n".format(legitimate_logs))
        f.write("Generated {} CAP attacks.\n".format(caps))
        f.write("Generated {} logs.\n".format(logs))


# ----------- main -----------

if __name__ == "__main__":
    print("Apps:")
    print(apps)
    print("-------------------")
    print("App under test:")
    print(new_app)
    print("-------------------")
    print("Data stores:")
    print(stores)
    print("-------------------")
    print("APIs:")
    print_apis()
    print("-------------------")
    legitimate_logs = gen_legitimate_logs()

    prob = input("Enter the probability for a CAP attack: ")
    if not prob.isdigit() and int(prob) < 0 or int(prob) > 100:
        print("The probability value must be an integer between 0 and 100.")
        sys.exit()

    caps = gen_cap_logs(p=int(prob))
    logs = create_log_file()
    generate_test_summary(prob, legitimate_logs, caps, logs)