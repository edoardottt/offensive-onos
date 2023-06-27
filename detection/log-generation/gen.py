'''
Generate logs for CAP attack simulation

https://github.com/edoardottt/offensive-onos
'''

# ----------- import -----------

import random
import sys

# ----------- global variables -----------

apps = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm']
stores = ['n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

new_app = random.choice(apps)

def gen_apis():
    '''
    This function generates a random API dictionary.
    '''
    result = {}
    i = 0
    for store in stores:
        i+=1
        result[i] = ['w', store]
        i+=1
        result[i] = ['r', store]
    return result

apis = gen_apis()
accessible_apis_n = 4 # number of APIs a single app can access

ms_start = 0            # start time (in milliseconds)
ms_end = 10000000       # end time (in milliseconds)
cap_interval = 1000     # time between a potential CAP attack and another (in ms)
api_interval = 100      # time between an API call and another (in ms)
log_file = "test.log"

cap_lengths = [3]


# ----------- functions -----------

def gen_legitimate_logs():
    '''
    This function generates random logs for legitimate apps.
    '''
    count = 0
    with open(log_file, 'w+') as f:
        for app in apps:
            if app != new_app:
                ts_app = random.randint(0, api_interval)
                accessible_apis = random.choices(list(apis.keys()), k = accessible_apis_n)
                while ts_app <= ms_end:
                    log_elements = [str(ts_app+api_interval), app, str(random.choice(accessible_apis)), "params"]
                    log = " ".join(log_elements)
                    f.write(log + "\n")
                    ts_app = ts_app + api_interval
                    count += 1
                    sys.stdout.flush()
                    print("Generated {} logs from legitimate apps!".format(count), flush=True, end="\r")
    f.close()
    print()


def gen_cap_logs(p = 50):
    '''
    This function generates CAP attack logs based on the
    probability value passed as input.
    '''
    pass
    count = 0
    with open(log_file, 'w+') as f:
        ts_app = random.randint(0, cap_interval)
        accessible_ds = ['n', 'o']
        while ts_app <= ms_end:
            if random.randint(0, 100) < p:
                # cap
                cap_elements = [new_app, random.choice(apps), random.choice(cap_lengths)]
                print(cap_elements)
                pass
            else:
                # no cap (random API call)
                api = random_api_call(random.choice(accessible_ds))
                log_elements = [str(ts_app+api_interval), new_app, str(api), "params"]
                log = " ".join(log_elements)
                f.write(log + "\n")

            ts_app = ts_app + cap_interval

            """
            log = " ".join(log_elements)
            f.write(log + "\n")
            ts_app = ts_app + api_interval
            count += 1
            sys.stdout.flush()
            print("Generated {} logs!".format(count), flush=True, end="\r")
            """
    # at each step (defined by cap_interval):
    # check if new CAP or not
    # if not: the app A performs a random API call
    # if CAP: generate a CAP tuple and add that to log file


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


if __name__ == "__main__":
    for x in apis:
        print(x, apis[x])
    print("-------------------")
    #gen_legitimate_logs()
    #gen_cap_logs()
    print(gen_cap_sequence('e', ['n', 'o'], 7))