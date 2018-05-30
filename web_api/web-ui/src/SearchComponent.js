import React from 'react';
import {Container, Row, Col} from 'reactstrap';
import {
    InputGroup,
    Input,
    InputGroupAddon,
    Button,
    Badge
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
                this.setState({
                    data: result,
                    button_enabled: true
                });
            });
    }

    onClick(event) {
        event.preventDefault()
        this.api.queryByRelevantTerms(this.state.queryterms)
        .then((result) => {
            this.setState({
                data: result,
                button_enabled: true
            });
        });
    }

    inputChange(event) {
        event.preventDefault();
        this.setState({
            queryterms: event.target.value,
            button_enabled: false
        });
    }

    render() {

        const graph = this.state.data.success ? (
                <GraphComponent display_detail={false} data={this.state.data}/>
            ) : (
                <div>
                  <MDSpinner size={100} />
                </div>
            )

        const button = this.state.button_enabled ? (
            <InputGroupAddon addonType="append"><Button onClick={this.onClick}>Search</Button></InputGroupAddon>
        ) : (
            <InputGroupAddon addonType="append"><Button onClick={this.onClick} >Search</Button></InputGroupAddon>
        )

        return (
            <div className="">
                <Row>
                     <Container className="">
                        <Col sm="12" md={{size: 8, offset: 2}}>
                            <InputGroup onClick={(e) => e.preventDefault()}>
                                <Input id="search" value={this.state.queryterms} onChange={this.inputChange}/>
                                { button }
                            </InputGroup>
                        </Col>
                    </Container>
                </Row>
                <Row>
                    <Container className="">
                        <Badge color="danger">Public Administration</Badge>
                        <Badge color="primary">Company</Badge>    
                    </Container>       
                </Row>
                <Row className="top-buffer">       
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
