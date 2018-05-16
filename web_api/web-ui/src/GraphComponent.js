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
        	node_detail: ''
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
    	console.log(n);
    	this.setState({
    		node_detail: n
    	});

    }

	render() {
		let data = this.genRandomTree();
		if (this.props.data.success){
			data = this.props.data.result;
		}
		if (this.state.node_detail != '')
			console.log(this.state.n);

		return (
			<div>
				<Row>
					<Col xs="9">
						<ForceGraph2D
						  height = {800}
				          graphData={data}
				          nodeLabel="fiscal_code"
				          nodeAutoColorBy="struct_type"
				          linkDirectionalParticles="value"
				          backgroundColor = "white"
				          nodeColor = "#dc3545"
				          onNodeClick = {this.onNodeClick}
				          showNavInfo = {true}
				          nodeRelSize = {6}
				          linkDirectionalParticleSpeed={d => d.value * 0.005}
				        />
				     </Col>
				     <Col xs="3">
				     	<NodeDetails />
				     </Col>
			     </Row>
		     </div>
			);
	}
}

export { GraphComponent }