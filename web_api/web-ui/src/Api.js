
class Api {
  constructor() {
    this.url = '/graph'
    if (process.env.NODE_ENV === "development"){
      this.url = "http://localhost:5000/graph"
    }
  }

  /**
  * returns a promise with the data or error
  **/
  queryByRelevantTerms(queryterms) {
    const post_data = JSON.stringify({
      "queryterms": queryterms
    });

    return fetch(this.url, {
        method: "POST",
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: post_data
      }).then((response) => {
        return response.json();
      });
  }
}

export { Api }