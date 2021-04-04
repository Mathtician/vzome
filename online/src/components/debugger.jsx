import React, { useState } from 'react';
import { connect } from 'react-redux'

import { withStyles, makeStyles } from '@material-ui/core/styles'
import Toolbar from '@material-ui/core/Toolbar'
import Grid from '@material-ui/core/Grid'
import TreeView from '@material-ui/lab/TreeView'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import ChevronRightIcon from '@material-ui/icons/ChevronRight'
import TreeItem from '@material-ui/lab/TreeItem'
import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import RedoRoundedIcon from '@material-ui/icons/RedoRounded'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'
import PublishRoundedIcon from '@material-ui/icons/PublishRounded'
import FastForwardRoundedIcon from '@material-ui/icons/FastForwardRounded'
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import Paper from '@material-ui/core/Paper'

import * as designFns from '../bundles/designs.js'
import * as dbugger from '../bundles/dbugger.js'
import { vZomeJava } from '@vzome/react-vzome'

const StyledTableCell = withStyles((theme) => ({
  head: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  body: {
    fontSize: 14,
  },
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
  root: {
    '&:nth-of-type(odd)': {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);

const useStyles = makeStyles((theme) => ({
  debuggerSource: {
    flex: '1 1 auto',
    display: 'flex',
    flexDirection: 'column',
    maxHeight: '720px'  // This is a horrible hack, but I haven't found anything else to control the expanded tree item height
  },
  treeview: {
    overflow: 'auto',
  },
}))

const Debugger = ( { data, current, branches, designName, doDebug } )  =>
{
  const classes = useStyles();
  const [ element, setElement ] = useState( null )

  const onLabelClick = element => event =>
  {
    event.preventDefault()
    setElement( element )
  }

  const renderTree = ( element ) => {
    const children = []
    let child = element.firstElementChild
    while ( child ) {
      children.push( child )
      child = child.nextElementSibling
    }
    return (
      <TreeItem key={element.id} nodeId={element.id} label={element.nodeName} onLabelClick={onLabelClick(element)}>
        { children.length > 0 ? children.map( child => renderTree( child ) ) : null }
      </TreeItem>
    )
  }

  const expanded = branches && [ ':', ...branches ]

  if ( !data )
    return null

  return (
    <Grid container direction='column'>
      <Grid item>
        <Toolbar id="debugger-tools" variant='dense'>
          <Tooltip title="Step in" aria-label="step-in">
            <IconButton color="secondary" aria-label="step-in" onClick={()=>doDebug(designName, vZomeJava.Step.IN)}>
              <GetAppRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step over" aria-label="step-over">
            <IconButton color="secondary" aria-label="step-over" onClick={()=>doDebug(designName, vZomeJava.Step.OVER)}>
              <RedoRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step out" aria-label="step-out">
            <IconButton color="secondary" aria-label="step-out" onClick={()=>doDebug(designName, vZomeJava.Step.OUT)}>
              <PublishRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Continue" aria-label="continue">
            <IconButton color="secondary" aria-label="continue" onClick={()=>doDebug(designName, vZomeJava.Step.DONE)}>
              <FastForwardRoundedIcon/>
            </IconButton>
          </Tooltip>
        </Toolbar>
      </Grid>
      <Grid item id="debugger-source" className={classes.debuggerSource}>
        <TreeView className={classes.treeview} selected={current} expanded={expanded}
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpanded={ expanded }
          defaultExpandIcon={<ChevronRightIcon />}
        >
          {renderTree( data, '' ) }
        </TreeView>
      </Grid>
      <Grid item style={{ flex: 0, height: '100px' }}>
        <TableContainer component={Paper}>
          <Table size="small" aria-label="command attributes">
            <TableHead>
              <TableRow>
                <StyledTableCell>Name</StyledTableCell>
                <StyledTableCell>Value</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {element && element.getAttributeNames().map( name => (
                ( name !== 'id' ) &&
                <StyledTableRow key={name}>
                  <StyledTableCell component="th" scope="row">
                    {name}
                  </StyledTableCell>
                  <StyledTableCell>{element.getAttribute( name )}</StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Grid>
    </Grid>
  )
}


const select = ( state ) =>
{
  const dbugger = state.designs && designFns.selectDebugger( state )
  if ( ! dbugger ) {
    return {} // document had an unknown field, or couldn't parse
  }
  return {
    data: dbugger.source,
    current: dbugger && dbugger.currentElement && dbugger.currentElement.id,
    branches: dbugger && dbugger.branchStack && dbugger.branchStack.map( ({ branch }) => branch.id ),
    designName: dbugger && designFns.selectDesignName( state )
  }
}

const boundEventActions = {
  doDebug : dbugger.debug,
}

export default connect( select, boundEventActions )( Debugger )
