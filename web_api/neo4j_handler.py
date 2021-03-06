from py2neo import Graph
import time
from lru import LRU

class Node(object):
    def __init__(self, type_id, node_type, id_s, fiscal_code, relevant_terms, 
            region, province, city, address, istat_code, adm_code, name, company_type, nation):
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
        self.name = name
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
        self.lru_cache = LRU(100)


        # MATCH (a:Node)-[r]->(b:Node) where a.relevant_terms CONTAINS 'vino' or b.relevant_terms CONTAINS 'vino' RETURN a,r,b LIMIT 10
        # MATCH (a:Node)-[r]->(b) where a.id_s > 0 and a.id_s < 100 and b.id_s > 0 and b.id_s < 100 return a,r,b LIMIT 1000

    def _create_query_relevant_terms(self, query_terms, limit=10000):
        base_query = "MATCH  (a:Node)-[r]->(b:Node) "
        base_where = " (a.relevant_terms contains '{}' and b.relevant_terms contains '{}') and "
        base_return = " return a,r,b limit {};"

        composed_query = base_query
        term_split = query_terms.split()
        print(term_split)

        if len(term_split) > 0:
            composed_query += " WHERE "

        for term in query_terms.split():
            mod_term = ' {}:'.format(term)
            composed_query += base_where.format(mod_term, mod_term)

        if len(term_split) > 0:
            composed_query = composed_query[:-4]

        composed_query += base_return.format(limit)
        print('query ' + composed_query)
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
            name = self._get_or_else(props['name'], ''),
            company_type = self._get_or_else(props['company_type'], ''),
            nation = self._get_or_else(props['nation'], ''),
        )
        return node

    def _create_link(self, src_id, rel, dst_id):
        props = dict(rel)
        return Link(src_id, dst_id, props['score'])

    def query_by_relevant_terms(self, query_terms, limit=1000):
        start = time.time()
        if query_terms in self.lru_cache:
            end = time.time()
            print('query in time {}'.format(end - start))
            return self.lru_cache.get(query_terms)
        else:
            querystring = self._create_query_relevant_terms(query_terms, limit)

            nodes = set()
            links = set()

            for src, rel, dst in self.graph.run(querystring):
                    src_node = self._create_node(src)
                    dst_node = self._create_node(dst)
                    if len(str(src_node.id)) > 0 and len(str(dst_node.id)) > 0:
                        link = self._create_link(src_node.id, rel, dst_node.id)
                        nodes.add(src_node)
                        nodes.add(dst_node)
                        links.add(link)

            end = time.time()
            print('query in time {}'.format(end - start))
            result =  Result(list(nodes), list(links))
            self.lru_cache[query_terms] = result
            return result

