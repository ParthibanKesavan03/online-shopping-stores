import React, { Component } from "react";

export default class Default extends Component {
  render() {
    return (
      <div className="container">
        <div className="row">
          <div className="col-10 mx-auto text-center text-primary text-capitalize pt-5">
            <h1 className="display-3">404</h1>
            <h1>ERROR</h1>
            <h3>
              the requested URL '
              <span className="text-danger">
                {this.props.location.pathname}
              </span>{" "}
              ' not found
            </h3>
          </div>
        </div>
      </div>
    );
  }
}
