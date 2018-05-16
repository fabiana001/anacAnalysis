from py2neo import Graph
import json


class Node(object):
    def __init__(self, id_s, struct_type, text, relevant_terms, fiscal_code):
        self.id = id_s
        self.struct_type = struct_type
        self.text = text
        self.relevant_terms = relevant_terms
        self.fiscal_code = fiscal_code

    def __repr__(self):
        return str(self.__dict__)

    def __eq__(self, other):
        return self.id == other.id

    def __str__(self):
        return str(self.__dict__)

    def __hash__(self):
        return hash((self.id, self.struct_type, self.fiscal_code))


class Link(object):
    def __init__(self, src_id, dst_id, score):
        self.source = src_id
        self.target = dst_id
        self.value = score

    def __repr__(self):
        return str(self.__dict__)

    def __eq__(self, other):
        return self.source == other.source and self.target == other.target

    def __hash__(self):
        return hash((self.source, self.target, self.value))


class Result(object):
    def __init__(self, nodes, links):
        self.nodes = nodes
        self.links = links

    def to_json(self):
        nodes = [n.__dict__ for n in self.nodes]
        links = [e.__dict__ for e in self.links]

        return {'nodes': nodes, 'links': links}



class Py2NeoHandler(object):
    def __init__(self, host, user, pwd):
        self.graph = Graph(host=host, user=user, password=pwd)
        self.relevant_terms_query = "MATCH  (n:Struttura)-[r:SEMANTIC]->(b) WHERE n.relevant_terms contains '{}' return n,r,b limit 1;"
        self.fiscal_code_query = "MATCH  (n:Struttura)-[r:SEMANTIC]->(b) WHERE n.codice_fiscale contains '{}' return n,r,b limit 1;"

    def _create_query_relevant_terms(self, query_terms, limit=1000):
        base_query = "MATCH  (n:Struttura)-[r:SEMANTIC]->(b)"
        base_where = " WHERE n.relevant_terms contains '{}' or "
        base_return = "return n,r,b limit {};"

        composed_query = base_query
        for term in query_terms.split(' '):
            composed_query += base_where.format(term)
        composed_query = composed_query[:-4]
        composed_query += base_return.format(limit)
        return composed_query

    def _get_or_else(self, value, default):
        if value:
            return value
        else:
            return default

    def _enum_struct_type(self, t):
        if t == 'AGG':
            return 4
        else:
            return 8

    def _create_node(self, n):
        props = dict(n)

        node = Node(
            id_s=self._get_or_else(props['id_s'], ''),
            struct_type=self._enum_struct_type(self._get_or_else(props['tipo_struttura'], 0)),
            text=self._get_or_else(props['oggetto'][:200], ''),
            relevant_terms=self._get_or_else(props['relevant_terms'], ''),
            fiscal_code=self._get_or_else(props['codice_fiscale'], '')
        )
        return node

    def _create_link(self, src_id, rel, dst_id):
        props = dict(rel)
        return Link(src_id, dst_id, props['score'])

    def query_by_relevant_terms(self, query_terms, limit=1000):
        querystring = self._create_query_relevant_terms(query_terms, limit)

        nodes = set()
        links = set()

        for src, rel, dst in self.graph.run(querystring):
            src_node = self._create_node(src)
            dst_node = self._create_node(dst)
            link = self._create_link(src_node.id, rel, dst_node.id)

            nodes.add(src_node)
            nodes.add(dst_node)
            links.add(link)

        return Result(list(nodes), list(links))
