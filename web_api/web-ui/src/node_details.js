import React from 'react';
import { Card, CardBody, CardSubtitle, CardTitle, CardText, CardLink, CardHeader } from 'reactstrap';

class NodeDetails extends React.Component {

	constructor(props) {
		super(props);
		this.paCard = this.paCard.bind(this);
		this.companyCard = this.companyCard.bind(this);
	}

	paCard(node) {
	      let card = 
		      <Card>
		        <CardHeader>{node.istat_code}</CardHeader>
		        <CardBody>
		          <CardTitle>{node.name}</CardTitle>
		          <CardText><b>fiscal_code:</b> {node.fiscal_code}</CardText>
		          <CardText><b>Address: {this.props.queryterms}</b> {node.address}, {node.city}, {node.province}, {node.region} </CardText>
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

	companyCard(node) {
	      let card = 
		      <Card>
		        <CardHeader>{node.company_type}</CardHeader>
		        <CardBody>
		          <CardTitle>{node.name}</CardTitle>
		          <CardText><b>fiscal_code:</b> {node.fiscal_code}</CardText>
		          <CardText><b>Address:</b>  {node.address}, {node.city}, {node.province}, {node.region}, {node.nation} </CardText>
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
		let card = undefined;

		if (node.node_type === 'PA'){
			card = this.paCard(node);
		} else {
			card = this.companyCard(node);
		}

		console.log(this.props);

		return (
		    <div>

		    	{ this.paCard(node) }
		    </div>
		  );
	}
}

export { NodeDetails }