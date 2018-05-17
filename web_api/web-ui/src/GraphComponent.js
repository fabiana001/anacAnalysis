import React from 'react';
import {ForceGraph2D, ForceGraph3D, ForceGraphVR} from 'react-force-graph';
import { Row, Col } from 'reactstrap';
import { NodeDetails } from './node_details';

class GraphComponent extends React.Component {
	constructor(props){
		super(props);
		//it expect props.data to contain the graph nodes
        this.genRandomTree = this.genRandomTree.bind(this);
        this.onNodeClick = this.onNodeClick.bind(this);
        this.state = {
        	node_detail: '',
        	colors: ['#28a745', '#007bff']
        };
	}

    genRandomTree(N = 300) {
        return {
            nodes: [...Array(N).keys()].map(i => ({id: i})),
            links: [...Array(N).keys()]
                .filter(id => id)
                .map(id => ({
                    source: id,
                    group: 1,
                    target: Math.round(Math.random() * (id - 1))
                }))
        }
    }

    onNodeClick(n) {
    	this.setState({
    		node_detail: n
    	});

    }

	render() {
		let data = this.genRandomTree();
		if (this.props.data.success){
			data = this.props.data.result;
		}

		return (
			<div>
				<Row>
					<Col xs="9">
						<ForceGraph2D
						  height = {800}
				          graphData={data}
				          nodeLabel="fiscal_code"
				          linkDirectionalParticles="value"
				          nodeColor = {(node) => {
				          		return this.state.colors[node.type_id];
				          	}
				          }
				          onNodeClick = {this.onNodeClick}
				          nodeRelSize = {6}
				          linkDirectionalParticleSpeed={d => d.value * 0.005}
				        />
				     </Col>
				     {this.state.node_detail != '' && 
					     <Col xs="3">
					     	<NodeDetails node={this.state.node_detail} />
					     </Col>
				 	  }
			     </Row>
		     </div>
			);
	}
}

export { GraphComponent }