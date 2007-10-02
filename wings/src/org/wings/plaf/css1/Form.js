function getEvent(event) {
  if (window.event)
    return window.event;
  else
    return event;
}

function getTarget(event) {
    if (event.srcElement)
        return event.srcElement;
    else
        return event.target;
}

function fireEvent( elementID, eventType ) {
  var o = document.getElementById(elementID);
  if (document.createEvent) {
    var evt = document.createEvent("Events");
    evt.initEvent("change", true, true);
    o.dispatchEvent(evt);
  } else if (document.createEventObject) {
    var evt = document.createEventObject();
    o.fireEvent('on' + "change", evt);
  }
}

// Submit a form instead of following a href textlink.
function submitForm(event, eventName, eventValue) {
    event = getEvent(event);
    var form = getParentByTagName(getTarget(event), "FORM");

    if ( form != null ) {
        // Add event request as hidden input element at form
        var eventNode = document.createElement("input");
        eventNode.setAttribute('type', 'hidden');
        eventNode.setAttribute('name', eventName);
        eventNode.setAttribute('value', eventValue);
        form.appendChild(eventNode);
        // simulate pressing of invisible submit-button
        // this avoids that wings thinks that the return button was pressed
        for(j=0;j<document.getElementsByName("js_submit").length;++j) {
            var submitButton=document.getElementsByName("js_submit")[j];
	    if (submitButton && typeof submitButton.form=='object' && submitButton.form==form) {
               submitButton.click(); // this submits
	    }
        }               
    } else {
        alert("form not found for " + getTarget(event));
    }

    return false;
}

// Submit a form instead of following a href textlink.
function commandlessSubmitForm(event) {
    event = getEvent(event);
    if ( event != null  ) {
        var form = getParentByTagName(getTarget(event), "FORM");

        if ( form != null ) {
            // simulate pressing of invisible submit-button
            // this avoids that wings thinks that the return button was pressed
            for(j=0;j<document.getElementsByName("js_submit").length;++j) {
                var submitButton=document.getElementsByName("js_submit")[j];
                if (submitButton && typeof submitButton.form=='object' && submitButton.form==form) {
                submitButton.click(); // this submits
                }
            }               
        } else {
            alert("form not found for " + getTarget(event));
        }
    }

    return false;
}

function getParentByTagName(element, tag) {
  while (element != null) {
    if (tag.toUpperCase() == element.tagName.toUpperCase())
      return element;
    element = element.parentNode;
  }
  return null;
}


/* Gain focus on the component with the given id. */
function gainFocus(id) {
    var element = document.getElementById(id);
    if (element && element.focus)
       element.focus();
    return true;
}
