#extension GL_OES_EGL_image_external : require
precision highp float;
varying vec2 vTexCoord;
uniform samplerExternalOES sTexture;
void main() {
      float oriX = vTexCoord.x;
      float oriY =  vTexCoord.y;

      //oriX=1.0-oriX;   //flip

      //if (oriX < 0.5) {  //duplicate
      //  oriX = 2.0 * oriX;
      // } else {
      //  oriX = 2.0 * oriX  - 1.0;
      // }

    gl_FragColor=texture2D(sTexture, vec2(oriX,oriY));
}