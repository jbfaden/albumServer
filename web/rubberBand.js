

 function startRubber (evt) {
   if (document.all) {
     var r = document.all.rubberBand;
     r.style.width = 0;
     r.style.height = 0;
     r.style.pixelLeft = event.x;
     r.style.pixelTop  = event.y;
     r.style.visibility = 'visible';
   }
   else if (document.getElementById) {
     var r = document.getElementById('rubberBand');
     r.style.width = 0;
     r.style.height = 0;
     r.style.left = evt.clientX + 'px';
     r.style.top = evt.clientY + 'px';
     r.style.visibility = 'visible';
   }
   else if (document.layers) {
     var r = document.rubberBand;
     r.clip.width = 0; r.clip.height = 0;
     r.left = evt.x;
     r.top = evt.y;
     r.visibility = 'show';
   }
   if (document.layers)
     document.captureEvents(Event.MOUSEMOVE);
   document.onmousemove = moveRubber;
 }

 function moveRubber (evt) {

   if (document.all) {
     var r = document.all.rubberBand;
     r.style.width = event.x - r.style.pixelLeft;
     r.style.height = event.y - r.style.pixelTop;
   }
   else if (document.getElementById) {
     //alert(document.getElementById('log'));
     document.getElementById('log').innerHTML= ""+r.style.width+" "+r.style.height;
     var r = document.getElementById('rubberBand');
     r.style.width = evt.clientX - parseInt(r.style.left);
     r.style.height = evt.clientY - parseInt(r.style.top);
   }
   else if (document.layers) {
     var r = document.rubberBand;
     r.clip.width = evt.x - r.left;
     r.clip.height = evt.y - r.top;
     r.document.open();
     r.document.write('<TABLE WIDTH="' + r.clip.width + '" HEIGHT="' + r.clip.height + '" BORDER="1"><TR><TD><\/TD><\/TR><\/TABLE>');
     r.document.close();
   }
 }

 function stopRubber (evt) {
   if (document.layers)
     document.releaseEvents(Event.MOUSEMOVE);
   document.onmousemove = null;
 }

 document.onmousedown = startRubber;
 document.onmouseup = stopRubber;
