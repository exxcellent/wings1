
var TREE_NODES = new Array();


function Tree(treeId, eventId, expandIcon, collapseIcon, baseAddress, rootNode) {
    this.eventId = eventId;
    this.expandIcon = expandIcon;
    this.collapseIcon = collapseIcon;
    this.treeElement = document.getElementById(treeId);
    this.rootNode = rootNode;
    this.rootNode.tree = this;
    this.baseAddress = baseAddress;
 }

Tree.prototype.getRootNode = function() {
    return this.rootNode;
}


function TreeNode(nodeId, expanded, toggleSelectionEvent, toggleExpansionEvent) {
    this.nodeId = nodeId;
    this.nodeElement = document.getElementById(nodeId);
    this.icon = document.getElementById(nodeId + "_icon");
    this.originalExpanded = expanded;
    this.expanded = expanded;
    this.children = null;
    this.toggleSelectionEvent = toggleSelectionEvent;
    this.toggleExpansionEvent = toggleExpansionEvent;
    this.parentNode = null;
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


