import React from 'react';
import { Card, CardBody, CardSubtitle, CardTitle, CardText, CardLink, CardHeader } from 'reactstrap';

class NodeDetails extends React.Component {

	constructor(props) {
		super(props);
		this.paCard = this.paCard.bind(this);
	}


	paCard(node) {
	      let card = 
		      <Card>
		        <CardHeader>Public Administration</CardHeader>
		        <CardBody>
		          <CardTitle>{node.istat_code}</CardTitle>
		          <CardText><b>fiscal_code:</b> {node.fiscal_code}</CardText>
		          <CardText><b>Address:</b> {node.address}, {node.city}, {node.province}, {node.region} </CardText>
		        </CardBody>
		        <CardBody>
		          <CardSubtitle>Relevant Terms</CardSubtitle>
		          <CardText>{node.relevant_terms}</CardText>
		        </CardBody>
		      </Card>

      return (
      		card
      	);

	}

	render() {
		let node = this.props.node;
		console.log(this.props.node);
		return (
		    <div>
		    	{ this.paCard(node) }
		    </div>
		  );
	}
}

export { NodeDetails }