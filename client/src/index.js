import React from 'react';
import { Provider } from 'react-redux'
import { render } from 'react-dom'
import * as serviceWorker from './serviceWorker';
import {
  BrowserRouter as Router,
  Switch,
  Route
} from "react-router-dom"
import logger from 'redux-logger'
import thunk from 'redux-thunk'

import App from './App';
import Article from './Article';
import createBundleStore from './bundles'

const store = createBundleStore( [ thunk, logger ] );

render(
  <Provider store={store}>
    <Router>
      <Switch>
        <Route path="/app" exact>
          <App />
        </Route>
        <Route path="/app/fivecell">
          <Article />
        </Route>
      </Switch>
    </Router>
  </Provider>,
  document.getElementById( 'root' )
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
