import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {
    InputGroup,
    Input,
    InputGroupAddon,
    Button
} from 'reactstrap';

import { Api } from './Api';
import { GraphComponent }  from './GraphComponent';
import MDSpinner from "react-md-spinner";

export default class SearchComponent extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
        this.inputChange = this.inputChange.bind(this);
        this.api = new Api();
        this.state = {
            queryterms: '',
            dropItemValue: '',
            data: {
                success: false
            }
        };
        this.api.queryByRelevantTerms(this.state.queryterms)
            .then((result) => {
                console.log('first result');
                this.setState({
                    data: result
                });
            });
    }

    onClick(event) {
        event.preventDefault()

        this.api.queryByRelevantTerms(this.state.queryterms)
        .then((result) => {
            console.log('first result');
            this.setState({
                data: result
            });
        });
    }

    inputChange(event) {
        event.preventDefault();
        this.setState({
            queryterms: event.target.value
        });
    }

    render() {

        const graph = this.state.data.success ? (
                <GraphComponent data={this.state.data}/>
            ) : (
                <div>
                  <MDSpinner size={100} />
                </div>
            )

        return (
            <div className="">
                <Row>
                    <Container className="">
                        <Col sm="12" md={{size: 8, offset: 2}}>
                            <InputGroup onClick={(e) => e.preventDefault()}>
                                <Input id="search" value={this.state.queryterms} onChange={this.inputChange}/>
                                <InputGroupAddon addonType="append"><Button onClick={this.onClick}>Search</Button></InputGroupAddon>
                            </InputGroup>
                        </Col>
                    </Container>
                </Row>
                <Row  className="top-buffer">
                </Row>
                <Row>
                    <Col>
                        { graph }
                    </Col>
                </Row>
            </div>
        );
    }
}
