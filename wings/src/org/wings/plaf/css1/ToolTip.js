/*
    Autor: Struppi <jstruebig@web.de>
    Beschreibung: Ein Tooltip.
*/

//document.write( '<style type="text/css">\n#tooltip{position:absolute;\ntop:0;left:0;visibility:hidden;}\n<\/style>');
document.write( '<div id="tooltip"><\/DIV>');

////////////////////////////////////////////////////////////
// getDocSize(window)
// Anmerkung (22.4.204) Evtl. muss der mode abgefragt werden

function getDocSize(w)
{
    if(!w) w = window;
    var pos = {w:0,h:0};
    if (typeof document.height != 'undefined')
    {
        pos =  { w: w.document.width, h: w.document.height};
    }
    else if (document.body && typeof document.body.scrollHeight != 'undefined')
    {
        pos.w = w.document.body.scrollWidth;
        pos.h = w.document.body.scrollHeight;
    }
    return pos;
}

////////////////////////////////////////////////////////////
// getWinSize(window)
function getWinSize(win)
{
    if(!win) win = window;
    var pos = {w:0,h:0};
    if(typeof win.innerWidth != 'undefined')
    {
        pos = { w: win.innerWidth, h: win.innerHeight};
    }
    else if (win.document.body)
    {
       pos.w = parseInt(win.document.body.clientWidth);
       pos.h = parseInt(win.document.body.clientHeight);
    }
    return pos;
}
////////////////////////////////////////////////////////////
// offset(window)
function pageOffset(win)
{
    if(!win) win = window;
    var pos = {left:0,top:0};
    var m = Mode() == 'Strict';

    pos.left = typeof win.pageXOffset != 'undefined' ? win.pageXOffset : // Mozilla/Netscape
    m ?  // IE im strict Modus
    document.documentElement.scrollLeft : // Strict Modus
    win.document.body.scrollLeft // Quirks Modus
    ;

    pos.top = typeof win.pageYOffset != 'undefined' ? win.pageYOffset :
    m  ?
    document.documentElement.scrollTop :// Strict Modus
    win.document.body.scrollTop // Quirks Modus
    ;

    return pos;
}

///////////////////////////////////////////////////////////
// setProp(id, prop, attr, win)

function setProp(id, prop, attr, win)
{
     var style = getStyle(id, win);
     if(!style) return null;
     if(typeof style[prop] == 'undefined' ) return null;
     if(typeof attr != 'undefined' ) style[prop] = attr;
     return style[prop];
}

///////////////////////////////////////////////////////////
// getStyle(id)
function getStyle(id, win)
{
    if(!id) return null;
    if(typeof id == "string") id = getById(id, win);
    if(typeof id.style == 'undefined') return id;
    return id.style;
}
///////////////////////////////////////////////////////////
// getById(id [, win])

function getById(id, win)
{
    var doc = window.document;
    if(win) doc = win.document;

    if(typeof id == 'undefined') return null;

    var obj = null;
    if(document.getElementById) obj = doc.getElementById(id);
    else if(typeof document.layers != 'undefined')  obj = _findObj_(id, doc);
    else if(document.all) obj = doc.all[id];

    return obj;
}
////////////////////////////////////////////////////////////
// Ein Layer beschreiben
function print(obj, text, win)
{
    if(!obj) return;
    if(typeof obj == "string") obj = getById(obj, win);

    if(typeof obj.innerHTML != 'undefined')
    {
       obj.innerHTML = text;
    }
    else if(typeof document.layers != 'undefined')
    {
         obj.document.open('text/html');
         obj.document.write(text);
         obj.document.close();
    }

}
////////////////////////////////////////////////////////////
// Ein Layer fixieren

function fixLayer(id, win)
{
    if(!win) win = window;
    var obj = getById(id, win);
    if(!obj) return alert('Fehler\n\nID:' + id + ' ist nicht zu finden.');

    _STICKY_OBJ_[_STICKY_OBJ_.length] = new Sticky(obj, win);

    _fixLayer_();
    if(_STICKY_OBJ_.length > 1) return obj;

    if (typeof win.onscroll != 'undefined') win.onscroll = _fixLayer_;
    else if(typeof document.layers != 'undefined')  setInterval ('_fixLayer_()', 50);
    else window.setInterval ('_fixLayer_()', 50);
    return obj
}

////////////////////////////////////////////////////////////
// setVis
function setVis(obj, mode, win)
{
    if(!obj) return null;
    if(typeof obj == "string") obj = getStyle(obj, win);
    var vis;

    if(typeof document.layers != 'undefined')
    {
         vis = mode ? 'show' : 'hidden';
         obj['visibility'] = vis;
         return obj['visibility'];
    }
    vis = mode ? 'visible' : 'hidden';
    obj['visibility'] =  vis;
    return obj['visibility']
}
////////////////////////////////////////////////////////////
// getSize(obj)
function getSize(obj, win)
{
    if(!obj) return null;
    if(typeof obj == "string") obj = getById(obj, win);

    var size = {width:0, height:0};

    if(typeof document.layers != 'undefined')
    {
         size.width = obj.clip.width;
         size.height = obj.clip.height;
    }
    else if(obj.offsetWidth)
    {
         size.width = parseInt(obj.offsetWidth);
         size.height = parseInt(obj.offsetHeight);
    }

    return size;
}

////////////////////////////////////////////////////////////
// pagePos(obj [,y, x])

function pagePos(obj, top, left, win)
{
    if(typeof obj == "string") obj = getById(obj, win);

    if(!win) win = window;
    if(!obj) return null;
    var pos;
    if(typeof left != 'undefined' && typeof top != 'undefined')
    {
         if(typeof document.layers != 'undefined')
         {
              obj.moveTo(left , top);
         }
         else
         {
              obj.style.left = left + 'px';
              obj.style.top = top + 'px';
         }
    }
    pos = {left:0, top:0};

    if(typeof obj.offsetLeft != 'undefined')
    {
         while (obj)
         {
             pos.left += obj.offsetLeft;
             pos.top += obj.offsetTop;
             obj = obj.offsetParent;
         }
    }
    else
    {
        pos.left = obj.left ;
        pos.top = obj.top ;
    }

    return pos;
}
function Mode(doc)
{
    if(!doc) doc = window.document;
    return (doc.compatMode && doc.compatMode == "CSS1Compat") ?  // strict Modus
    'Strict' : 'Quirks';
}

/* Globale Hilfsfunktionen */

////////////////////////////////////////////////////////////
// Nur für den NC 4.x um Layer zu finden.

function _findObj_(n, doc)
{
    if(doc[n]) return doc[n];

    for(var i = 0; i < doc.layers.length; i++)
    {
         if(typeof doc.layers[i].document != 'undefined')
         {
              var obj = _findObj_(n, doc.layers[i].document);
              if(obj) return obj;
         }
    }
    return null;
}


////////////////////////////////////////////////////////////
// Sticky -> Hilfsstruktur um die Startwerte zu sichern

function Sticky(obj, win)
{
    var pos = pagePos(obj);

    this.top = pos.top;
    this.left = pos.left;
    this.obj = obj;
    this.win = win;
}
////////////////////////////////////////////////////////////
// Die eigentliche Funktion um einen Layer zu fixieren
var c = 0;

function _fixLayer_()
{
    for(var i = 0; i < _STICKY_OBJ_.length; i++)
    {
       var l = _STICKY_OBJ_[i];
       if(!l) continue;
       var offset = pageOffset(l.win);
       pagePos(l.obj, l.top + offset.top, l.left + offset.left, l.win);
    }
}
var _STICKY_OBJ_ = new Array(); // benötigt fixLayer

///////////////////////////////////////////////////////////
// mouseEvent()
function mouseEvent(e, was, ins, wo)
{
    if(!wo) wo = window.document;
    // was muss eine Referenz auf eine Funktion sein.

    // Einige Vorbereitungen für NC 4.x
    if(typeof document.layers != 'undefined')
    {
          var evt = '';
          // Netscape 4 fängt nicht das onclick event.
          if(e == 'click') e = 'mousedown';

          if(e == 'mousedown') evt = Event.MOUSEDOWN;
          else if(e == 'mousemove') evt = Event.MOUSEMOVE;
          else if(e == 'mouseover') evt = Event.MOUSEOVER;
          else if(e == 'mouseout') evt = Event.MOUSEOUT;

          wo.captureEvents(evt);
    }
    else if(window.addEventListener && e == 'dblclick')
    {
          wo.addEventListener('dblclick',_event_func, false);
    }
    var event = 'on' + e;
    // Die Events sammeln
    if(typeof wo.hook == 'undefined') wo.hook = new Array();
    if(typeof wo.hook[e] == 'undefined')
    {
        wo.hook[e] = new Array();
        // alten Event sichern
        if(typeof wo[event] == 'function') wo.hook[e][wo.hook.length] = wo[event];
        wo[event] = _event_func;
    }
    if(ins) wo.hook[e][was] = was;
    else delete wo.hook[e][was];
    // alert(event + '\n' + _event_func);
}
///////////////////////////////////////////////////////////
// getMousePos()
function getMousePos()
{
    return _MOUSE_POS_;
}
//////////////////////////////////////////////////////////
// Die event Funktion
function _event_func(e)
{

    if(!e) e = window.event;
    for(var i in this.hook[e.type]) this.hook[e.type][i](e, this);
    return true;
}

///////////////////////////////////////////////////////////
// _mouse_pos_
var _MOUSE_POS_ = {left:0,top:0};

function _mouse_pos(evt, obj)
{
    if(!evt) return true;

    _MOUSE_POS_.left = evt.clientX;
    _MOUSE_POS_.top = evt.clientY;

    // Der IE 6 braucht Sonderbehandlung
    if(document.compatMode && document.compatMode == "BackCompat")
    {
        // IE 6 im Quirks-(BackCompat) Modus
        _MOUSE_POS_.scrollLeft =  _MOUSE_POS_.left + document.body.scrollLeft;
        _MOUSE_POS_.scrollTop = _MOUSE_POS_.top + document.body.scrollTop;
    }
    else if (document.compatMode && document.compatMode == "CSS1Compat") {
        // IE 6 im CSS1Compat Modus
        _MOUSE_POS_.scrollLeft= _MOUSE_POS_.left + document.documentElement.scrollLeft;
        _MOUSE_POS_.scrollTop = _MOUSE_POS_.top + document.documentElement.scrollTop;
    }else if (document.body)
    {
        // IE < 6 (nicht sicher da ich nur den IE 4 hab)
        _MOUSE_POS_.scrollLeft = _MOUSE_POS_.left + document.body.scrollLeft;
        _MOUSE_POS_.scrollTop  = _MOUSE_POS_.top + document.body.scrollTop;
    }
    return true;
}

 mouseEvent('mousemove', _mouse_pos, true);


////////////////////////////////////////////////////////////
// Tooltip
var _FEHLER_ = false;
var _SHOW_ = false;
var tttimeout = null;

function showTooltip(show, t)
{
    var obj = getById('tooltip');
    if(!obj)
    {
       if(!_FEHLER_) alert('FEHLER!!!\n\nDu musst einen Layer\nmit der ID="ToolTip" definieren!');
       _FEHLER_ = true;
       return false;
    }
    // Tooltip wieder verstecken
    if(!show)
    {
       if (tttimeout != null) clearTimeout(tttimeout);
       setVis('tooltip', false);
       _SHOW_ = false;
       return false;
    }

    // ist der Tooltip berits offen?
    if(_SHOW_ == true) return false;
    _SHOW_ = true;

    // neuer Text
    print(obj, t);

    // position berechnen, relativ zum Mauszeiger
    var m_pos = getMousePos();
    var s_tooltip = getSize(obj);

    var tooltip_y = m_pos.scrollTop - s_tooltip.height - 5 ;
    var tooltip_x = m_pos.scrollLeft - s_tooltip.width ;

    if(tooltip_x < 0) tooltip_x = 2;
    if(tooltip_y < 0) tooltip_y = 2;

    //window.status = "Pos:" + tooltip_y +  ':' + tooltip_x;
    var pos = pagePos(obj, tooltip_y, tooltip_x);
    tttimeout = setTimeout("setVis('tooltip', true)", 1000);

    return true;
}
