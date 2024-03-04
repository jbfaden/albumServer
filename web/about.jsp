<%-- 
    Document   : about
    Created on : Jan 11, 2016, 9:53:29 AM
    Author     : jbf
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>About this version</title>
    </head>
    <body>
        <h1>Thanks to open-source:</h1><ul>
            <li>metadata extractor: https://github.com/drewnoakes/metadata-extractor</li>
            <li>commonmark: https://github.com/commonmark/commonmark-java</li>
            <li>totem-video-thumbnailer</li>
        </ul>
        
        <h1>AlbumServer</h1>
        <h3>20240228.1</h3></ul> 
            <li>Don't throw runtime when credentials info file isn't found, it's better to work somewhat and introduce errors to future me.
        </ul>
        <h3>20220925a</h3></ul> 
            <li>introduce box, a special album which can contain other albums.
        </ul>
        <h3>20220915a</h3></ul> 
            <li>corrections to exif handling.
        </ul>
        <h3>20220914a</h3></ul> 
            <li>add search box for album name.  
            <li>corrections to last release.
        </ul>
        <h3>20220913a</h3><ul>
            <li>corrections to video tabs.
            <li>password file is kept in home now, and cache can always be deleted.
            <li>use GitLab to provide notes.
            <li>correction where ARGB was used to create JPG images, which isn't supported but was not caught by older Java instances.
        </ul>
        <h3>20160126a</h3><ul>
            <li>experiments with orientation, though I think this is still broken.</li>
            <li>read and provide link for GPS metadata.</li>
            <li>add "edit" to album notes</li>
        </ul>
        <h3>20160120a</h3><ul>
            <li>corrections to videos.</li>
            <li>remove extra newline in edit textarea.</li>
        </ul>
        <h3>20160119b</h3><ul>
            <li>corrections to hidden attribute and search.</li>
        </ul>
        <h3>20160119a</h3><ul>
            <li>sort by picture id when all names are of the same length.</li>
        </ul>
        <h3>20160117a</h3>
        <ul>
            <li>Fix for-dumb bug that prevented some images from working (Apache error visible).</li>
            <li>New icons for next previous.</li>
            <li>next/prev icons are to the left at Sarah's request, and larger.</li>
            <li>"calculating" icon now instructs to reload.</li>
            <li>remove hide and like/dislike from public login.</li>
            <li>Captions appear in index.</li>
        </ul>
        <h3>20160111b</h3>
        <ul>
            <li>found at /home/jbf/ct/netbeansProjects/fun/albumServer/
            <li>does not appear to appear to live in a revision control system.
            <li>minor updates include:</li>
            <li>sorting directory listings</li>
            <li>timestamps in ids are used, when available.</li>
            <li>correcting the "calculating" icon, where the font was tiny.</li>
        </ul>
        <p><em><a href='AlbumServerContent0.jsp'>Return</a> to content.</em></p>
    </body>
</html>
