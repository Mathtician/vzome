
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;

public class Branch implements UndoableEdit
{
	private final Context context;
	
    public Branch( Context context )
    {
		super();
		this .context = context;
	}

	private List<UndoableEdit> edits = new ArrayList<>();
    private XmlSaveFormat format;
    private Element xml;
    
    public void addEdit( UndoableEdit edit )
    {
        edits .add( edit );
    }

    @Override
    public boolean isDestructive()
    {
        return false;
    }

    @Override
    public void perform() throws Failure
    {
        final Stack<UndoableEdit> toUndo = new Stack<>();
        NodeList nodes = xml .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node kid = nodes .item( i );
            if ( kid instanceof Element ) {
                Element editElem = (Element) kid;

                // the remainder is essentially identical to DeferredEdit .redo()

                UndoableEdit edit = context .createEdit( editElem, format .groupingDoneInSelection() );
                addEdit( edit );
                edit. loadAndPerform(editElem, format, new UndoableEdit.Context()
                {
                    @Override
                    public void performAndRecord( UndoableEdit edit )
                    {
                        // realized is responsible for inserting itself, or any replacements (migration)
                        try {
                            edit .perform();
                        } catch (Failure e) {
                            // really hacky tunneling
                            throw new RuntimeException( e );
                        }
                        toUndo .push( edit );
                    }

                    @Override
                    public UndoableEdit createEdit( Element xml, boolean groupInSelection )
                    {
                        return context .createEdit( xml, groupInSelection );
                    }
                } );
            }
        }
        while ( ! toUndo .isEmpty() )
        {
            UndoableEdit edit = toUndo .pop();
            edit .undo();
        }
    }

    @Override
    public void undo() {}

    @Override
    public void redo() {}

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public Element getXml( Document doc )
    {
        Element branch = doc .createElement( "Branch" );
        for (UndoableEdit edit : edits) {
            branch .appendChild( edit .getXml( doc ) );
        }
        return branch;
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        Element branch = doc .createElement( "Branch" );
        for (UndoableEdit edit : edits) {
            branch .appendChild( edit .getDetailXml( doc ) );
        }
        return branch;
    }

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, final Context context ) throws Failure
    {
        this .xml = xml;
        this .format = format;
        context .performAndRecord( this );
    }

    @Override
    public boolean isSticky()
    {
        return true;
    }
}
