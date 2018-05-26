#extension GL_OES_EGL_image_external : require
precision highp float;
varying vec2 vTexCoord;
uniform samplerExternalOES sTexture;


vec2 toPolar(vec2 point) {
    float r = length(point);
    float theta = atan(point.y,point.x);           //这其实就是atan2，返回的是-pi到pi
    return vec2(r, theta);
}

void main() {
   float x=vTexCoord.x,y=vTexCoord.y;

  //distortion
  if(1.0>0.5){
    vec2 polarBase=toPolar(vec2(-0.4,3.6));                           //矩形扇形坐标变换
    vec2 polarCur=toPolar(vec2(x-0.5,y+3.6));
    float baseR=polarBase.x,baseTheta=polarBase.y,curR=polarCur.x,curTheta=polarCur.y;

    highp vec2 p2=vec2((baseTheta-curTheta)/(2.0*baseTheta-3.1415926),4.0*(curR/baseR-1.0));
    if (all(greaterThanEqual(p2, vec2(0.0)))&&all(lessThanEqual(p2, vec2(1.0)))){
        x = p2.x;
        y = p2.y;
    } else{
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
  }

  x=1.0-x;
 //end
  gl_FragColor = texture2D(sTexture, vec2(x,y));
}

