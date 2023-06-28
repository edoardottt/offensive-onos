"""
Analyze logs for CAP attacks detection

https://github.com/edoardottt/offensive-onos
"""

# ----------- import -----------

import sys
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
    g.vs["name"] = stores + apps
    g.vs["objtype"] = ["S"]*13 + ["A"] * 13
    g.es["count"] = list(edges.values())

    # Plot in matplotlib
    # Note that attributes can be set globally (e.g. vertex_size), 
    # or set individually using arrays (e.g. vertex_color)
    fig, ax = plt.subplots(figsize=(40, 40))
    matplotlib.use('TkAgg')
    ig.plot(
        g,
        target=ax,
        layout="auto",
        vertex_size=0.5,
        vertex_color=[
            "steelblue" if objtype == "S" else "salmon" for objtype in g.vs["objtype"]
        ],
        vertex_frame_width=8.0,
        vertex_frame_color="white",
        vertex_label=g.vs["name"],
        vertex_label_size=13.0,
        edge_label_size=15.0,
        edge_label=g.es["count"],
    )

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

def get_cap_gadgets(g, new_app):
    """
    This function returns ...
    """
    temp = []
    for store in stores:
        temp.extend(ig.Graph.get_all_simple_paths(g, 
                                                  object_id_dict[new_app], 
                                                  object_id_dict[store])
                                                  )

    result = [elem for elem in temp if len(elem) > 3]
    return result


def find_cap(caps, time_section):
    """
    This function returns ...
    """
    pass


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

    gadgets = get_cap_gadgets(g, new_app)
    print(len(gadgets))
    plot(g, edges)