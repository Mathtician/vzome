
import React from 'react'
import { connect } from 'react-redux'
import { commandTriggered } from '../bundles/mesh'
import Dropdown from 'react-bootstrap/Dropdown'

const EditMenu = ({ visible, edits, doEdit }) =>
{
  if ( visible )
    return (
      <Dropdown>
        <Dropdown.Toggle variant="success" id="dropdown-basic">
          Edits
        </Dropdown.Toggle>

        <Dropdown.Menu>
          <Dropdown.Item onClick={ e => doEdit( 'random' ) }>New Random</Dropdown.Item>
          <Dropdown.Item onClick={ e => doEdit( 'allSelected' ) }>Select All</Dropdown.Item>
          <Dropdown.Item onClick={ e => doEdit( 'allDeselected' ) }>Deselect All</Dropdown.Item>
          <Dropdown.Item onClick={ e => doEdit( 'centroid' ) }>Centroid 1</Dropdown.Item>
          <Dropdown.Item onClick={ e => doEdit( 'ShowPoint', { mode: 'origin' } ) }>Show Origin</Dropdown.Item>
          <Dropdown.Item onClick={ e => doEdit( 'Delete' ) }>Delete</Dropdown.Item>
          <Dropdown.Divider />
          { edits.map( edit =>
            <Dropdown.Item onClick={ e => doEdit( edit ) }>{edit}</Dropdown.Item>
          ) }
        </Dropdown.Menu>
      </Dropdown>
    )
  else
    return null
} 

const select = ( { jre, implementations, mesh } ) => ({
  visible: jre.javaReady && implementations.supportsEdits,
  edits: Object.getOwnPropertyNames( mesh.commands )
})

const boundEventActions = {
  doEdit : commandTriggered,
}

export default connect( select, boundEventActions )( EditMenu )
