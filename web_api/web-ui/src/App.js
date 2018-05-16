import React, {Component} from 'react';
import './App.css';
import {Collapse, Navbar, NavbarToggler, Nav, NavItem, NavLink} from 'reactstrap';
import SearchComponent from './SearchComponent';

class App extends Component {
    constructor(props) {
        super(props);

        this.toggleNavbar = this.toggleNavbar.bind(this);
        this.state = {
            collapsed: true
        };
    }

    toggleNavbar() {
        this.setState({
            collapsed: !this.state.collapsed
        });
    }

    render() {
        return (
            <div className="App">
                <header className="bg-primary">
                    <div className="container text-white pt-6 pb-6">
                        <h1 className="display-4">Anac Exploration</h1>
                        <h5 className="m-0 ml-1">sub title</h5>
                    </div>
                </header>

                <Navbar color="faded" light className="navbar navbar-expand-lg navbar-dark bg-primary sticky-top">
                    <NavbarToggler onClick={this.toggleNavbar} className="mr-2"/>
                    <Collapse isOpen={!this.state.collapsed} navbar>
                        <Nav navbar>
                            <NavItem>
                                <NavLink className="nav-item nav-link text-white pl-3 pr-3 mr-3 border border-white"
                                         href="#search">Search</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink className="nav-item nav-link text-white pl-3 pr-3 mr-3 border border-white"
                                         href="#description">Description</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink className="nav-item nav-link text-white pl-3 pr-3 mr-3 border border-white"
                                         href="#methodology">Methodology and References</NavLink>
                            </NavItem>
                        </Nav>
                    </Collapse>
                </Navbar>

                <div className="">
                    <section>
                        <h3 id="search" className="pt-5 pb-3 text-primary">Search</h3>
                        <p className="text-secondary"> add a description </p>
                        {/* add the search box */}
                        <SearchComponent/>
                    </section>
                </div>

                <div className="container">
                    <section>
                        <h3 id="description" className="pt-5 pb-3 text-primary">Description</h3>
                        <p className="text-secondary"> add a description </p>
                        {/* add the search box */}
                    </section>
                </div>

                <div className="container">
                    <section>
                        <h3 id="methodology" className="pt-5 pb-3 text-primary">Methodology and References</h3>
                        <p className="text-secondary"> add a description </p>
                        {/* add the search box */}
                    </section>
                </div>
            </div>
        );
    }
}

export default App;
