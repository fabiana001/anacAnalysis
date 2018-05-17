from py2neo import Graph
import json


class Node(object):
    def __init__(self, type_id, node_type, id_s, fiscal_code, relevant_terms, 
            region, province, city, address, istat_code, adm_code, company_name, company_type, nation):
        self.type_id = type_id
        self.node_type = node_type
        self.id = id_s
        self.fiscal_code = fiscal_code
        self.relevant_terms = relevant_terms
        self.region = region
        self.province = province
        self.city = city
        self.address = address
        self.istat_code = istat_code
        self.administrative_code = adm_code
        self.company_name = company_name
        self.company_type = company_type
        self.nation = nation


    def __repr__(self):
        return str(self.__dict__)

    def __eq__(self, other):
        return self.id == other.id

    def __str__(self):
        return str(self.__dict__)

    def __hash__(self):
        return hash((self.id, self.fiscal_code))


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
        self.relevant_terms_query = "MATCH  (n:Node)-[r:semantic_connected]->(b) WHERE n.relevant_terms contains '{}' return n,r,b limit 1;"
        self.fiscal_code_query = "MATCH  (n:Node)-[r]->(b) WHERE n.fiscal_code contains '{}' return n,r,b limit 1;"

    def _create_query_relevant_terms(self, query_terms, limit=10000):
        base_query = "MATCH  (n:Node)-[r]->(b)"
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

    def _create_node(self, n):
        props = dict(n)

        node = Node(
            type_id =  self._get_or_else(props['type_id'], ''),
            node_type = self._get_or_else(props['node_type'], ''),
            id_s = self._get_or_else(props['id_s'], ''),
            fiscal_code = self._get_or_else(props['fiscal_code'], ''),
            relevant_terms = self._get_or_else(props['relevant_terms'], ''),
            region = self._get_or_else(props['region'], ''),
            province = self._get_or_else(props['province'], ''),
            city = self._get_or_else(props['city'], ''),
            address = self._get_or_else(props['address'], ''),
            istat_code = self._get_or_else(props['istat_code'], ''),
            adm_code = self._get_or_else(props['administrative_code'], ''),
            company_name = self._get_or_else(props['company_name'], ''),
            company_type = self._get_or_else(props['company_type'], ''),
            nation = self._get_or_else(props['nation'], ''),
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
