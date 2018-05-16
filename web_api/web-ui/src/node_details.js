import React from 'react';
import { Card, CardBody, CardSubtitle, CardTitle, CardText, CardLink } from 'reactstrap';

class NodeDetails extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		return (
		    <div>
		      <Card>
		        <CardBody>
		          <CardTitle>Card title</CardTitle>
		          <CardSubtitle>Card subtitle</CardSubtitle>
		        </CardBody>
		        <CardBody>
		          <CardText>Some quick example text to build on the card title and make up the bulk of the card's content.</CardText>
		          <CardLink href="#">Card Link</CardLink>
		          <CardLink href="#">Another Link</CardLink>
		        </CardBody>
		      </Card>
		    </div>
		  );
	}
}

export { NodeDetails }