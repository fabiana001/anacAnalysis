import React from 'react';
import {ForceGraph2D, ForceGraph3D, ForceGraphVR} from 'react-force-graph';
import {Row, Col, Badge} from 'reactstrap';
import {NodeDetails} from './node_details';

class GraphComponent extends React.Component {
    constructor(props) {
        super(props);
        //it expect props.data to contain the graph nodes
        this.genRandomTree = this.genRandomTree.bind(this);
        this.onNodeClick = this.onNodeClick.bind(this);
        this.state = {
            colors: ['blue', 'red'],
            node_detail: undefined,
            data: props.data,
            display_detail: props.display_detail
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            colors: ['blue', 'red'],
            node_detail: undefined,
            data: this.props.data,
            display_detail: nextProps.display_detail
        });
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
            node_detail: n,
            display_detail: true
        });

    }

    render() {
        let data = this.genRandomTree();
        if (this.props.data.success) {
            data = this.props.data.result;
        }

        let card = <div></div>
        if (this.state.node_detail && this.state.display_detail) {
            card =
                <Col xs="3">
                    <NodeDetails node={this.state.node_detail} queryterms={this.state.data.queryterms}/>
                </Col>
        }

        return (
            <div>
                <Row>
                    <Col xs="9">
                        <ForceGraph2D
                            height={800}
                            graphData={data}
                            nodeLabel="name"
                            linkDirectionalParticles="value"
                            nodeColor={(node) => {
                                return this.state.colors[node.type_id];
                            }
                            }
                            onNodeClick={this.onNodeClick}
                            nodeRelSize={6}
                            linkDirectionalParticleSpeed={d => d.value * 0.005}
                        />
                    </Col>
                    {card}
                </Row>
            </div>
        );
    }
}

export {GraphComponent}