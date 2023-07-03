"""
Analyze logs for CAP attacks detection

https://github.com/edoardottt/offensive-onos
"""

# ----------- import -----------

import sys
from datetime import datetime
import igraph as ig
import matplotlib
import matplotlib.pyplot as plt


# ----------- global variables -----------

apps = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm']
stores = ['n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

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

log_file = "test.log"
object_id_dict = {}
id_object_dict = {}
edges = {}


# ----------- graph construction -----------

def init_dictonaries():
    """
    This function initializes all the dictionaries needed 
    to build the graph (+ translation between edges).
    """
    objects = apps + stores
    for c,e in enumerate(objects):
        object_id_dict[e] = c
        id_object_dict[c] = e
    for a in apps:
        for s in stores:
            edges[(object_id_dict[a],object_id_dict[s])] = 0
            edges[(object_id_dict[s],object_id_dict[a])] = 0


def read_log():
    """
    This function reads the log file and returns
    a dictionary representing the edges.
    """
    with open(log_file, "r") as f:
        lines = f.readlines()
        for line in lines:
            parts = line.split(" ")
            app = parts[1]
            api = parts[2]
            edge = to_edge(app, api)
            edges[edge] += 1
    return edges


def to_edge(app, api):
    """
    This function translates a log entry into an edge 
    of the graph.
    """
    action, store = apis[int(api)]
    app_id = object_id_dict[app]
    store_id = object_id_dict[store]
    if action == 'w':
        return (app_id, store_id)
    else:
        return (store_id, app_id)


def clean_empty_edges(edges):
    """
    This function takes as input a dictionary of edges and returns a dictionary
    without empty values (e.g. an edge with a count of 0).
    """
    result = {}
    for item in edges.items():
        if item[1] != 0:
            result[item[0]] = item[1]
    return result


def get_n_vertices():
    """
    This function returns the number of elements
    of the graph.
    """
    result = set()
    for key in edges:
        if key[0] not in result:
            result.add(key[0])
        if key[1] not in result:
            result.add(key[1])
    return len(result)


def build_graph():
    """
    This function creates a bipartite graph using the edges dictionary.
    If you want a normal graph use get_n_vertices() as first parameter.
    """
    g = ig.Graph.Bipartite([0]*13+[1]*13, list(edges.keys()), directed=True)
    return g


def plot(g, edges):
    """
    This function plots the graph using matplotlib.
    """
    g["title"] = "CAP gadgets graph"
    g.vs["name"] = apps + stores
    g.vs["objtype"] = ["A"]*13 + ["S"] * 13
    g.es["count"] = list(edges.values())

    # Plot in matplotlib
    # Note that attributes can be set globally (e.g. vertex_size), 
    # or set individually using arrays (e.g. vertex_color)
    fig, ax = plt.subplots(figsize=(100, 100))
    matplotlib.use('TkAgg')
    ig.plot(
        g,
        target=ax,
        layout="tree",
        vertex_size=0.4,
        vertex_color=[
            "steelblue" if objtype == "S" else "salmon" for objtype in g.vs["objtype"]
        ],
        vertex_frame_width=0.0,
        vertex_frame_color="white",
        vertex_label=g.vs["name"],
        vertex_label_size=10.0,
        edge_label_size=9.0,
        edge_width=0.6,
        edge_label=g.es["count"],
    )

    mng = plt.get_current_fig_manager()
    mng.resize(*mng.window.maxsize())

    # save_img(fig)
    plt.show()


def save_img(fig):
    """
    This function saves the graph as a png image
    and as GML file.
    """
    fig.savefig("test_log.png")
    ig.save("test_log.gml")


# ----------- graph analysis -----------

def get_cap_gadgets(g, new_app, cutoff):
    """
    This function returns ...
    """
    temp = []
    for store in stores:
        temp.extend(ig.Graph.get_all_simple_paths(g, 
                                                  object_id_dict[new_app], 
                                                  object_id_dict[store],
                                                  int(cutoff))
                                                  )

    result = [elem for elem in temp if len(elem) > 3]
    return result


def translate_cap_gadgets(gadgets):
    """
    This function translates CAP gadgets expresses in graph nodes
    to apps and stores proper names.
    """
    result = []
    for gadget in gadgets:
        new_gadget = [id_object_dict[elem] for elem in gadget]
        result += [new_gadget]
    return result


def find_api(action, store):
    """
    Return an API given an action (r/w) and a data store. 
    """
    for api in apis.keys():
        if apis[api][0] == action and apis[api][1] == store:
            return api
    return None


def cap_gadgets_to_api(gadgets):
    """
    This function translates CAP gadget sequences to a sequence
    of pair, apps and their associated APIs.
    e.g.
    ['b', 'r', 'a', 'n'] ---> [(b,9), (a,10), (a,1)]
    """
    result = []
    for gadget in gadgets:
        translated_gadget = []
        for i in range(0, len(gadget), 2):
            if i == 0:
                store = gadget[1]
                api = find_api('w', store)
                translated_gadget += [(gadget[0], api)]
            else:
                # read from previous store
                store = gadget[i-1]
                api = find_api('r', store)
                translated_gadget += [(gadget[i], api)]
                # write to next store
                store = gadget[i+1]
                api = find_api('w', store)
                translated_gadget += [(gadget[i], api)]
        result += [translated_gadget]

    return result


def get_log_info(line):
    """
    This function takes as input a log entry
    and returns the timestamp, the app and the API.
    """
    elements = line.split(" ")
    return int(elements[0]), elements[1], int(elements[2])


def find_cap(lines, i, gadget, time_section, start_ts):
    """
    This function tries to find the CAP gadget in 
    logs starting having the i-th log entry as first
    element of the gadget.
    """
    i_gadget = 1
    i+=1
    while i_gadget < len(gadget) and i < len(lines):
        timestamp, app, api = get_log_info(lines[i])
        if timestamp - start_ts > time_section:
            return False
        if gadget[i_gadget] == (app, api):
            i_gadget += 1
        i += 1

    return i_gadget == len(gadget)


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
        result += [apis[api_pairs[i][1]][1]]

    return result


def hashable_gadget(input_list):
    """
    This function takes as input a sequence of pair (apps 
    and their associated APIs) and returns an hashable string
    representing the CAP gadget.
    """
    result = ""
    cap_gadgets = api_to_cap_gadgets(input_list)
    for elem in cap_gadgets:
        result += str(elem)
    return result


def find_caps(gadgets, time_section):
    """
    This function returns the distribution of potentially
    exploited CAP attacks found in the log file.
    """
    start_time = datetime.now()
    print("CAP search in logs started at {}.".format(start_time))
    cap_distribution = {}
    with open(log_file, "r") as f:
        lines = f.readlines()
    # here search for CAPs in logs
    for i in range(len(lines)):
        sys.stdout.flush()
        print("> Scanning line {}/{}...".format(str(i+1), len(lines)), flush=True, end="\r")
        for gadget in gadgets:
            start_timestamp, app, api = get_log_info(lines[i])
            if gadget[0] == (app, api):
                found = find_cap(lines, i, gadget, time_section, start_timestamp)
                if found:
                    gadget_hash = hashable_gadget(gadget)
                    if gadget_hash in cap_distribution:
                        cap_distribution[gadget_hash] += 1
                    else:
                        cap_distribution[gadget_hash] = 1

    end_time = datetime.now()
    print()
    print("CAP search in logs took {}.".format(end_time - start_time))
    
    return cap_distribution


# ----------- result analysis -----------

def print_cap_distribution(cap_distribution):
    """
    This function prints the potentially exploited CAP attacks
    distribution sorted by items count.
    """
    sorted_cap_distribution = sorted(cap_distribution.items(), key=lambda x:x[1], reverse=True)
    result = dict(sorted_cap_distribution)
    for key, value in result.items():
        print(key, value)


def plot_top_cap_distribution(cap_distribution, k = 30):
    """
    This function plots the distribution of top k
    potentially exploited CAP attacks.
    """
    result = dict(sorted(cap_distribution.items(), key=lambda x:x[1], reverse=True)[:k])
    plt.bar(result.keys(), result.values(), 1.0, color='g')


# ----------- main -----------

if __name__ == "__main__":
    init_dictonaries()
    edges = clean_empty_edges(read_log())
    g = build_graph()

    # ask network operator to provide required input
    new_app = input("Which is the app under test? ")
    if new_app not in apps:
        print("Unknown application {}.".format(new_app))
        sys.exit()

    time_section = input("Enter the time section value: ")
    if not time_section.isdigit():
        print("The time section value must be an integer.")
        sys.exit()

    cutoff = input("Enter the maximum length for CAP vectors: ")
    if not cutoff.isdigit():
        print("The maximum length for CAP vectors value must be an integer.")
        sys.exit()

    gadgets = get_cap_gadgets(g, new_app, cutoff)
    print("Found {} CAP attack vectors!".format(str(len(gadgets))))

    cap_gadgets_apis = cap_gadgets_to_api(translate_cap_gadgets(gadgets))

    cap_distribution = find_caps(cap_gadgets_apis, int(time_section))

    print("Found {} potentially exploited CAP gadgets!".format(sum(cap_distribution.values())))
    plot_top_cap_distribution(cap_distribution)

    plot(g, edges)
