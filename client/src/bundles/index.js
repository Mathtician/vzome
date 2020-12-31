
import { createStore, applyMiddleware, combineReducers } from 'redux'

import * as alerts from './alerts'
import * as cheerpj from './cheerpj'
import * as camera from './camera'
import * as lighting from './lighting'
import * as progress from './progress'
import * as jsweet from './jsweet'
import * as commands from '../commands'
import * as workingPlane from './planes'
import * as models from './models'
import * as fields from '../fields'
import * as shapers from './shapers'

const requiredBundles = { lighting, fields }

let bundles
const urlParams = new URLSearchParams( window.location.search );
const profile = urlParams.get( "profile" ) || urlParams.get( "editMode" )
switch ( profile ) {

  case "plane":
    bundles = { ...requiredBundles, java: jsweet, models, shapers, workingPlane }
    break;

  case "jsweet":
    bundles = { ...requiredBundles, java: cheerpj, camera, alerts, progress }
    break;

  default:
    bundles = { ...requiredBundles, java: jsweet, models, shapers, commands, alerts, progress }
    break;
}


export default ( middleware ) =>
{
  const names = Object.keys( bundles )

  const reducers = names.reduce( ( obj, key ) => {
    const reducer = bundles[key].reducer
    if ( reducer )
      obj[ key ] = reducer
    return obj
  }, {} )

  console.log( `bundle reducers: ${JSON.stringify( Object.keys( reducers ) )}` )

  const rootReducer = combineReducers( reducers )
  
  const store = createStore( rootReducer, applyMiddleware( ...middleware ) );
  
  // TODO: is there a better pattern than these inits?
  names.map( key => {
    const init = bundles[key].init
    if ( init ) {
      console.log( `bundle init: ${key}` )
      init( window, store )
    }
    return null
  } )

  return store
}