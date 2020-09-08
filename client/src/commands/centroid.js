
export default ( state ) =>
{
  const { field, selected } = state
  const shown = new Map( state.shown )

  const scale = field.createRational( 1, selected.size )
  let sum = undefined
  for ( let [key, value] of selected ) {
    shown.set( key, value )
    if ( sum ) {
      sum = field.vectoradd( sum, value[0] )
    }
    else {
      sum = value[0]
    }
  }
  const centroid = [ field.scalarmul( scale, sum ) ] // canonically, all mesh objects are arrays of vectors
  const id = JSON.stringify( centroid )
  console.log( "%%%%%%%%%%%%%% centroid %%%%%%%%%%%%")

  return {
    ...state,
    shown,
    selected : new Map().set( id, centroid )
  }
}