<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <title>Simple GSP page</title>
    <g:meta name="layout" value="main" />
    <g:javascript library="prototype" />
    <g:javascript library="scriptaculous" />
    <cropper:head />
  </head>
  <body>
    <img id="castle" src="${resource(dir:'images/cropper', file:'castleMed.jpg')}" alt="A medium sized castle" />
    <cropper:crop imageId="castle">
      <cropper:onEndCrop>
        alert("yeah boi");
      </cropper:onEndCrop>
    </cropper:crop>
  </body>
</html>