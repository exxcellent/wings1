
var TREE_NODES = new Array();


function Tree(treeId, eventId, expandIcon, collapseIcon, hashMark, emptyIcon, indentDepth,
              baseAddress, rootNode) {
    this.eventId = eventId;
    this.expandIcon = expandIcon;
    this.collapseIcon = collapseIcon;
    this.hashMark = hashMark;
    this.emptyIcon = emptyIcon;
    this.indentDepth = indentDepth;
    this.treeElement = document.getElementById(treeId);
    this.rootNode = rootNode;
    this.rootNode.tree = this;
    this.baseAddress = baseAddress;
    this.maxDepth = this.getMaxDepthFromNode(rootNode);
 }

Tree.prototype.getRootNode = function() {
    return this.rootNode;
}


Tree.prototype.getMaxDepthFromNode = function(node) {
    if ( node.isLeaf() ) {
        return 1;
    } else { 
        var max = 0;
        for ( var i=0; i<node.getChildCount(); i++ ) {
            max = Math.max(this.getMaxDepthFromNode(node.getChildAt(i)), max);
        }
        return max+1;
    }
}

Tree.prototype.getMaxDepth = function() {
    return this.maxDepth;
}



function TreeNode(nodeId, expanded, toggleSelectionEvent, toggleExpansionEvent, content) {
    this.nodeId = nodeId;
    this.nodeElement = null;
    this.icon = null;
    this.originalExpanded = expanded;
    this.expanded = expanded;
    this.children = null;
    this.toggleSelectionEvent = toggleSelectionEvent;
    this.toggleExpansionEvent = toggleExpansionEvent;
    this.parentNode = null;
    this.content = content;
}

TreeNode.prototype.writeSubTree = function() {
    this.writeNode();
    if ( this.children!=null ) {
        for ( var i=0; i<this.children.length; i++ ) {
            this.children[i].writeSubTree();
        }
    }
}


TreeNode.prototype.writeNode = function() {
    var depth = this.getDepth();
    var maxDepth = this.getTree().maxDepth;

    var element = "<tr id=\"" + this.nodeId + "\"";
    if ( this.parentNode!=null && !this.parentNode.expanded ) {
        element += " style=\"display:none\"";
    }
    element += ">";

    /*
     * fill the indented area.
     */
    for ( var i=0; i<depth; ++i) {
        element += "<td";
        if ( !this.getParentAt(i).isLastChild() ) {
            element += " style=\"background-image:url(" + this.getTree().hashMark + ")\"";
        }
        element += ">";
        element += "<img"
            + " width=\"" + this.getTree().indentDepth + "\""
            + " height=\"1\"" 
            + " src=\"" + this.getTree().emptyIcon + "\""
            + "/>";
        element += "</td>";
    }

    /*
     * now, write the tree.
     */
    element += "<td";
    if ( maxDepth > (depth+1) ) {
        element += " colspan=\"" + (maxDepth - depth) + "\""; 
    }
    element += ">";

    if ( !this.isLeaf() ) {
        element += "<table cellspacing=\"0\"><tr><td>";
        element += "<a href=\"javascript:TREE_NODES['" + this.nodeId + "'].toggleExpansion()\">";

        element += "<img"
            + " id=\"" + this.nodeId + "_icon" + "\""
            + " border=\"none\""
            + " src=\"";
        if ( this.expanded) {
            element += this.getTree().collapseIcon;
        } 
        else {
            element += this.getTree().expandIcon;
        }
        element += "\"/>";
        element += "</a>";
        element += "</td><td>";
    }

    element += "<a href=\"javascript:TREE_NODES['" + this.nodeId + "'].toggleSelection()\">";
    element += "<nobr>" + this.content + "</nobr>";
    element += "</a>";

    if ( !this.isLeaf() ) {
        element += "</td></tr></table>";
    }

    // finalize row. add newline.
    element += "</td><td width=\"100%\"></td></tr>";

    document.write(element);

    this.nodeElement = document.getElementById(this.nodeId);
    this.icon = document.getElementById(this.nodeId + "_icon");
}

TreeNode.prototype.getDepth = function() {
    var depth = 0;
    var rootNode = this;
        
    while ( rootNode.parentNode != null ) {
        rootNode = rootNode.parentNode;
        depth++;
    }

    return depth;
}

TreeNode.prototype.getTree = function() {
    var rootNode = this;
        
    while ( rootNode.parentNode != null ) {
        rootNode = rootNode.parentNode;
    }

    return rootNode.tree;
}

TreeNode.prototype.addChild = function(child) {
    if ( this.children==null ) {
        this.children = new Array();
    }

    this.children[this.children.length] = child;
    child.parentNode = this;
}

TreeNode.prototype.updateIcon = function() {
    this.icon.src = this.expanded ? this.getTree().collapseIcon : this.getTree().expandIcon;
}

TreeNode.prototype.getNode = function(nodeId) {
    return TREE_NODES[nodeId];
}

TreeNode.prototype.collapse = function() {
    this.expanded = false;
    this.updateDisplayRecursive(false);
    this.updateIcon();
}

TreeNode.prototype.expand = function() {
    this.expanded = true;
    this.updateDisplayRecursive(true);
    this.updateIcon();
}

TreeNode.prototype.updateDisplayRecursive = function(visible) {
    var display = visible ? "inline" : "none";

    if ( this.children!=null ) {
        for ( var i=0; i<this.children.length; i++ ) {
            this.children[i].nodeElement.style.display = display;
            this.children[i].updateDisplayRecursive(visible && this.children[i].expanded);
        }
    }
}

TreeNode.prototype.toggleExpansion = function() {
    if ( this.expanded ) {
        this.collapse();
    } else {
        this.expand();
    }
}

TreeNode.prototype.getRootNode = function() {
    return this.getTree().rootNode;
}

TreeNode.prototype.getChildCount = function() {
    return this.children.length;
}

TreeNode.prototype.getChildAt = function(i) {
    return this.children[i];
}

TreeNode.prototype.getParentAt = function(i) {
    var c = this.getDepth() - i;
    var parent = this;

    while ( c>0 ) {
        parent = parent.parentNode;
        c--;
    }

    return parent;
}

TreeNode.prototype.isLastChild = function() {
    if ( this.parentNode==null ) return true;
    return this.parentNode.getChildAt(this.parentNode.getChildCount()-1) == this;
}

TreeNode.prototype.isLeaf = function() {
    return this.children == null || this.children.length == 0;
}


TreeNode.prototype.appendToggleExpansionEvents = function(address) {
    if ( this.children!=null ) {
        if ( this.expanded!=this.originalExpanded ) {
            // &amp; did not work here!!
            address += "&" + this.getTree().eventId + "=" + this.toggleExpansionEvent;
        } // end of if ()
    
        for ( var i=0; i<this.children.length; i++ ) {
            address = this.children[i].appendToggleExpansionEvents(address);
        }
    }

    return address;
}

TreeNode.prototype.toggleSelection = function() {
    var address = this.getTree().baseAddress;

    address += "?" + this.getTree().eventId + "=" + this.toggleSelectionEvent;

    address = this.getTree().getRootNode().appendToggleExpansionEvents(address);

    document.location.href = address;
}


