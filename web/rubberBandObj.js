// TODO: how to get the top and left of the parent!

// parent should be an image.
// rubberDiv should be an empty div with border
// logSpan is a span to put log information, if desirable.
function RubberBand( document, parent, rubberDiv, logSpan ) {
   this.parent= parent;
   this.parent.rubberBand= this;
   this.rubberDiv= rubberDiv; 
   this.logSpan= logSpan;
   this.documemt= document;
   parent.onmousedown= this.startRubber;
   document.onmouseup= this.stopRubber;
}


RubberBand.prototype.log= function( msg ) {
   if ( this.logSpan !=null ) this.logSpan.innerHTML= msg;
};

RubberBand.prototype.startRubber = function( evt ) {
     // this is parent.
     var me= this.rubberBand;
     me.log("start");
     var r = me.rubberDiv;
     r.style.width = 0;
     r.style.height = 0;

     pleft= 20;
     ptop= 20;
     r.style.left = pleft + evt.clientX + 'px';
     r.style.top = ptop + evt.clientY + 'px';
     r.style.visibility = 'visible';
     document.onmousemove = me.moveRubber;
     document.onmouseup= me.stopRubber
     document.rbandDragging= me;
     return true;
};

RubberBand.prototype.moveRubber = function( evt ) {
     // this is document.
     var me= this.rbandDragging;
     var r = me.rubberDiv;
     me.log(  ""+r.style.width+" "+r.style.height );

     pleft= 20;
     ptop= 20;

     r.style.width = pleft + evt.clientX - parseInt(r.style.left);
     r.style.height = ptop + evt.clientY - parseInt(r.style.top);
     return true;
};

RubberBand.prototype.stopRubber = function( evt ) {
     var me= this.rbandDragging;
     me.log( "stop" );
     document.onmousemove = null;
     document.rbandDragging= null;
     return true;
};

// returns object with the properties "left,top,width, and height"
RubberBand.prototype.getCoordinates= function() {
   var result= new Object();
   result.x= this.rubberDiv.style.left;
   result.y= this.rubberDiv.style.top;
   result.height=  this.rubberDiv.style.height;
   result.width= this.rubberDiv.style.width;
   return result;
};

RubberBand.prototype.clear= function() {
   this.rubberDiv.style.visibility='hidden';
}