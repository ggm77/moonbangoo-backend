import"./modulepreload-polyfill-P2Xu9kJm.js";import{$ as e,A as t,D as n,E as r,F as i,H as a,I as o,J as s,K as c,L as l,M as u,N as d,O as f,P as p,Q as m,R as h,S as g,T as _,U as v,V as y,W as b,_ as ee,at as te,b as ne,c as re,ct as ie,d as x,et as S,f as C,ft as w,g as ae,h as oe,it as T,j as se,l as ce,m as E,n as le,o as ue,ot as D,p as O,pt as k,q as de,r as fe,rt as pe,st as A,t as me,tt as he,v as ge,w as _e,x as ve,y as ye}from"./surface-U1VgzFBo.js";var j=[[4,1],[12,3.5],[16,4],[24,5],[40,8.58],[51,11.35],[54,11.89],[56,12.15],[59,12.65],[96,20.7],[105,21.3]],be=.1,xe=2.5,Se=j.map((e,t)=>{if(t===j.length-1)return be;let n=(j[t+1][1]-j[t][1])/(j[t+1][0]-j[t][0]);if(t===0)return n;let r=(j[t][1]-j[t-1][1])/(j[t][0]-j[t-1][0]);return 2*r*n/(r+n)}),Ce=[{height:12,name:`구름 위로`},{height:24,name:`우주`},{height:40,name:`달`},{height:51,name:`화성`},{height:54,name:`목성`},{height:56,name:`토성`},{height:59,name:`태양계 밖`},{height:96,name:`은하 밖`}];function we(e){let[t]=j[0];if(e<=t)return Math.max(0,e)*xe;for(let t=1;t<j.length;t++){let[n,r]=j[t],[i,a]=j[t-1];if(e>n)continue;let o=n-i,s=(e-i)/o,c=s*s,l=c*s;return 10**((2*l-3*c+1)*a+(l-2*c+s)*o*Se[t-1]+(-2*l+3*c)*r+(l-c)*o*Se[t])}let[n,r]=j[j.length-1];return 10**(r+(e-n)*be)}function Te(e){let t=0,n=200;for(let r=0;r<50;r++){let r=(t+n)/2;we(r)<e?t=r:n=r}return n}var Ee=94607e11,De=1e8*Ee,Oe=Te(De),M=e=>e<10?e.toFixed(1).replace(/\.0$/,``):Math.round(e).toLocaleString(`ko-KR`);function ke(e){if(e<1e3)return`${Math.round(e)} m`;if(e<1e7)return`${M(e/1e3)} km`;if(e<1e11)return`${M(e/1e7)}만 km`;if(e<.1*Ee)return`${M(e/1e11)}억 km`;let t=e/Ee;return t<1e4?`${M(t)} 광년`:t<1e8?`${M(t/1e4)}만 광년`:`${M(t/1e8)}억 광년`}var N=.5,P=.072,F=Oe+P/2,I=1/240,Ae=16,je=22,Me=42,Ne=14,Pe=1.2,Fe=2.6,Ie=7,Le=38,Re=.35,ze=.05,Be=.25,Ve=8,He=.55,Ue=1.5,We=1.2,Ge=48,Ke=1.6,qe=.38,Je=.8*Ae/N,Ye=6.9,Xe=new w(0,1,0),Ze=new w(1,0,0),Qe=new s,$e=new s().setFromAxisAngle(Ze,Math.PI);function et(e){return()=>{e=e+1831565813|0;let t=Math.imul(e^e>>>15,1|e);return t=t+Math.imul(t^t>>>7,61|t)^t,((t^t>>>14)>>>0)/4294967296}}var tt=class e{pos=new w(0,P/2,0);vel=new w;q=new s;omega=new w;phase=`rest`;face=`front`;bounces=0;rng;predicting=!1;acc=0;ceilingHit=!1;rubbed=0;wobble={t:0,dur:1,tilt0:0,tilt:0,fall:0,phi:0,psi:0,ticks:0};dq=new s;axis=new w;constructor(e=Math.random){this.rng=e}reset(){this.pos.set(0,P/2,0),this.vel.set(0,0,0),this.q.identity(),this.omega.set(0,0,0),this.phase=`rest`,this.face=`front`,this.bounces=0,this.ceilingHit=!1,this.rubbed=this.acc=0}normal(e=new w){return e.copy(Xe).applyQuaternion(this.q)}launch(e,t){return this.phase!==`rest`||t<Pe||t<Math.abs(e)?!1:(this.phase=`air`,this.bounces=0,this.ceilingHit=!1,this.rubbed=0,this.acc=0,this.vel.set(0,C(t*Fe,5,Ne),0),this.omega.set(-t*Ie*(.8+.4*this.rng()),e*4+(this.rng()-.5)*2,0),this.clampSpin(),!0)}get canSpin(){return this.phase===`air`&&this.bounces===0&&!this.ceilingHit}spin(e,t){this.canSpin&&(this.omega.x-=t*Le,this.omega.y+=e*Le,this.clampSpin(),this.rubbed+=Math.hypot(e,t))}step(e){let t=[];for(this.acc+=e;this.acc>=I;)this.acc-=I,this.phase===`air`?this.stepAir(t):this.phase===`settle`&&this.stepSettle(t);return t}clampSpin(){this.omega.length()>Me&&this.omega.setLength(Me)}stepAir(e){let t=Math.max(-18,this.vel.y-Ae*I);if(this.bounces===0){this.rubbed*=Math.exp(-.004166666666666667/Re);let e=this.rubbed/Re,n=Math.min(Ne,Ve*e),r=t<n?this.vel.y+(n-this.vel.y)*(1-Math.exp(-4*I)):t;this.vel.y=t+(Math.max(t,r)-t)*O(ze,Be,e)}else this.vel.y=t;let n=Math.max(0,F-this.pos.y);if(this.vel.y>0&&(this.vel.y=Math.min(this.vel.y,1.5+Math.sqrt(40*n))),this.pos.y+=this.vel.y*I,this.pos.y>=F&&this.vel.y>0){let t=this.vel.y;this.pos.y=F,this.vel.y=-6,this.omega.x-=10,this.clampSpin(),this.rubbed=0,this.ceilingHit=!0,e.push({type:`ceiling`,strength:Math.min(1,t/4)})}let r=this.omega.length(),i=this.bounces===0?Math.min(r,je):0;r>i&&this.omega.setLength(i+(r-i)*Math.exp(-.35*I));let a=this.omega.length();a>1e-6&&(this.dq.setFromAxisAngle(this.axis.copy(this.omega).divideScalar(a),a*I),this.q.premultiply(this.dq).normalize());let o=this.normal(this.axis).y,s=N*Math.sqrt(Math.max(0,1-o*o))+P/2*Math.abs(o);if(this.pos.y>s||this.vel.y>0)return;this.pos.y=s;let c=-this.vel.y;if(c>Ke){this.vel.y=c*He,this.omega.multiplyScalar(.7),this.bounces++===0&&!this.predicting?this.fairKick():this.omega.x+=(this.rng()-.5)*c*Ue,e.push({type:`bounce`,strength:Math.min(1,c/10)});return}this.startSettle(o),e.push({type:`settle`,face:this.face})}fairKick(){let e=this.rng()<.5?`front`:`back`,t=this.omega.x,n=We*2*Math.PI/(2*this.vel.y/Ae);for(let r=0;r<Ge;r++){this.omega.x=t+(this.rng()-.5)*n;let i=this.rng()*2**32>>>0;if(this.predict(i)===e||r===47){this.rng=et(i);return}}}predict(t){let n=new e(et(t));n.predicting=!0,n.phase=this.phase,n.bounces=this.bounces,n.pos.copy(this.pos),n.vel.copy(this.vel),n.q.copy(this.q),n.omega.copy(this.omega);let r=[];for(let e=0;e<2400&&n.phase===`air`;e++)n.stepAir(r);return n.face}startSettle(e){this.phase=`settle`,this.face=e>=0?`front`:`back`,this.vel.set(0,0,0);let t=this.wobble,n=this.normal().multiplyScalar(this.face===`front`?1:-1),r=-this.axis.copy(this.omega).cross(n).y/Math.max(Math.sqrt(1-n.y*n.y),.001);this.omega.set(0,0,0),t.phi=Math.atan2(-n.x,n.z),t.tilt=Math.acos(C(n.y,-1,1));let i=this.tiltQuat(t.phi,t.tilt).invert().multiply(this.q).multiply(this.base().clone().invert());t.psi=2*Math.atan2(i.y,i.w),t.fall=t.tilt>qe?Math.min(-.6,r):0,t.fall||this.startWobble()}startWobble(){let e=this.wobble;e.tilt0=e.tilt,e.t=0,e.dur=.9+4*e.tilt0+this.rng()*.3,e.ticks=Math.floor(e.phi/Math.PI)}base(){return this.face===`front`?Qe:$e}tiltQuat(e,t){return new s().setFromAxisAngle(new w(Math.cos(e),0,Math.sin(e)),t)}stepSettle(e){let t=this.wobble;if(t.fall){t.fall-=Je*Math.cos(t.tilt)*I,t.tilt+=t.fall*I,t.tilt<=qe&&(e.push({type:`bounce`,strength:Math.min(1,-t.fall/12)}),t.fall=0,this.startWobble()),this.pose();return}t.t+=I;let n=1-t.t/t.dur;if(n<=0){this.phase=`rest`,this.q.setFromAxisAngle(Xe,t.psi).multiply(this.base()),this.pos.y=P/2,e.push({type:`rest`,face:this.face});return}t.tilt=t.tilt0*Math.cbrt(n);let r=Ye/Math.sqrt(Math.max(t.tilt,.02));t.phi+=r*I,t.psi-=r*(1-Math.cos(t.tilt))*I;let i=Math.floor(t.phi/Math.PI);i!==t.ticks&&(t.ticks=i,e.push({type:`tick`,strength:t.tilt/qe,rate:r})),this.pose()}pose(){let e=this.wobble;this.q.copy(this.tiltQuat(e.phi,e.tilt)).multiply(this.dq.setFromAxisAngle(Xe,e.psi)).multiply(this.base()),this.pos.y=N*Math.sin(e.tilt)+P/2*Math.cos(e.tilt)}},nt=new URL(`/moonbangoo/assets/logo-mono-BTsCikKK.svg`,``+import.meta.url).href,rt=new URL(`/moonbangoo/assets/preem-mask-wRS7iPUd.webp`,``+import.meta.url).href,L=1024,it=.72,at={base:`#f3deae`,raised:`#fff5df`,light:1.15},ot=4.5,st=.9,ct=4,lt=3;function ut(e){let{c:t,ctx:n}=dt();return n.fillStyle=n.strokeStyle=`#fff`,e(n),t}function dt(){let e=document.createElement(`canvas`);return e.width=e.height=L,{c:e,ctx:e.getContext(`2d`)}}function ft(e,t){let{c:n,ctx:r}=dt();return r.drawImage(e,0,0),r.globalCompositeOperation=`source-in`,r.fillStyle=t,r.fillRect(0,0,L,L),n}async function pt(e,n){let r=new URL(e,location.href).pathname.endsWith(`.webp`),i=r?null:re(await(await fetch(e)).text()),a=r?await new t().loadAsync(e):null,o=i?i.viewBox.baseVal:{width:a.naturalWidth,height:a.naturalHeight},s=L*n/Math.max(o.width,o.height),c=Math.round(o.width*s),l=Math.round(o.height*s),u=a??await ce(i,c,l);return ut(e=>e.drawImage(u,(L-c)/2,(L-l)/2,c,l))}function mt(e){let{c:t,ctx:n}=dt(),r=n.createImageData(L,L);for(let t=0;t<L*L;t++)r.data[t*4]=r.data[t*4+1]=r.data[t*4+2]=Math.min(1,Math.max(0,e(t)))*255,r.data[t*4+3]=255;return n.putImageData(r,0,0),t}function ht(e,t){let n=dt();return n.ctx.fillStyle=at.base,n.ctx.fillRect(0,0,L,L),n.ctx.drawImage(ft(t,at.raised),0,0),x(n.c,e)}async function gt(e,t,n=it){let r=await pt(t,n),i=dt();i.ctx.fillStyle=`#000`,i.ctx.fillRect(0,0,L,L),i.ctx.drawImage(ft(r,`#fff`),0,0);let a=fe(i.c),o=mt(e=>.29+.26*a[e]),s=new me(L,L);return s.data.set(le(le(a,L,L,2),L,L,2)),s.add((e,t)=>{let n=Math.hypot(e+.5-L/2,t+.5-L/2)/(L/2);return ct*Math.min(n,st)**2+lt*O(st,.925,n)}),new y({map:ht(e,r),roughnessMap:x(o,e,!1),normalMap:x(s.toNormalCanvas(ot),e,!1),metalness:1,roughness:1})}function _t(t){let n=document.createElement(`canvas`);n.width=512,n.height=4;let r=n.getContext(`2d`);for(let e=0;e<64;e++){let t=r.createLinearGradient(e*512/64,0,(e+1)*512/64,0);t.addColorStop(0,`#000`),t.addColorStop(.5,`#fff`),t.addColorStop(1,`#000`),r.fillStyle=t,r.fillRect(e*512/64,0,8,4)}let i=x(n,t,!1);return i.wrapS=e,i.repeat.x=2,new y({color:at.base,metalness:1,roughness:.38,bumpMap:i,bumpScale:1.5})}async function vt(e){let[t,n]=await Promise.all([gt(e,nt),gt(e,rt,.86)]),r=[t,n,_t(e)],i=ue(e,`pack`);for(let e of r)Object.assign(e,{envMap:i,envMapIntensity:at.light});return r}var yt=new ve(N,96),bt=new _e(N,N,P,96,1,!0);function xt([e,t,n]){let r=new f,i=new l(yt,e);i.rotation.x=-Math.PI/2,i.position.y=P/2;let a=new l(yt,t);return a.rotation.x=Math.PI/2,a.position.y=-P/2,r.add(i,a,new l(bt,n)),r}var St=e=>[parseInt(e.slice(1,3),16),parseInt(e.slice(3,5),16),parseInt(e.slice(5,7),16)];function Ct(e,t,n){let r=new Uint8Array(e*t*4);for(let i=0;i<t;i++)for(let a=0;a<e;a++){let[o,s,c,l]=n(a+.5,i+.5),u=((t-1-i)*e+a)*4;r[u]=Math.round(o),r[u+1]=Math.round(s),r[u+2]=Math.round(c),r[u+3]=Math.round(l*255)}let i=new _(r,e,t);return i.colorSpace=he,i.magFilter=d,i.minFilter=p,i.generateMipmaps=!0,i.needsUpdate=!0,i}function wt(e,t){if(e<=t[0][0])return t[0][1];let n=t.findIndex(t=>t[0]>=e);if(n<0)return t[t.length-1][1];let[r,i]=t[n-1],[a,o]=t[n];return i+(o-i)*(e-r)/(a-r)}var Tt=100,Et=6371e3,Dt=1737e3,Ot=696e6,kt=1496e8,At=0x197a8f6dd551980000,jt=3844e5,Mt=2e7,Nt=0xe18398e7601900000,R=Math.PI/180,Pt=14*R+8*R,z=new w(0,Math.sin(Pt),Math.cos(Pt)),Ft=new w(0,Math.cos(Pt),-Math.sin(Pt)),It=new w(1,0,0),Lt=new w(0,Et,0),Rt=e=>z.clone().multiplyScalar(-Math.cos(e)).addScaledVector(Ft,Math.sin(e)),zt=Rt(40*R),Bt=Rt(55*R),Vt=.005,Ht=()=>zt.clone().multiplyScalar(kt),Ut=()=>Ht().addScaledVector(Bt,Nt),Wt=()=>new w().crossVectors(zt,It).normalize(),Gt=()=>new w().crossVectors(Bt,It).normalize(),Kt=[{name:`mercury`,r:244e4,orbit:579e8,angle:3.5,boost:.75},{name:`venus`,r:605e4,orbit:1082e8,angle:.4,boost:1},{name:`mars`,r:339e4,orbit:2279e8,angle:2.6,boost:.85},{name:`jupiter`,r:699e5,orbit:7785e8,angle:.5,boost:2.1},{name:`saturn`,r:582e5,orbit:1434e9,angle:4,boost:1.9},{name:`uranus`,r:254e5,orbit:2871e9,angle:5.2,boost:1.5},{name:`neptune`,r:246e5,orbit:4495e9,angle:2.1,boost:1.5}],qt=(e,t)=>Ht().addScaledVector(It,e*Math.cos(t)).addScaledVector(zt,e*Math.sin(t)),Jt=e=>Math.log10(Math.max(e,1)),Yt=e=>1-O(12.65,13.2,Jt(e)),Xt=(e,t)=>new w(Math.cos(e)*Math.cos(t),Math.sin(e),-Math.cos(e)*Math.sin(t)),Zt=36.02*R,Qt=129.36*R,$t=Xt(Zt,Qt);function en(){let e=Xt(Zt+Math.PI/2,Qt),t=new w().crossVectors(e,$t);return new i().setFromMatrix4(new o().makeBasis(t,$t,e.negate()))}function tn(e){return{cam:Lt.clone().addScaledVector(z,Math.max(e,1)),k:Tt/Math.max(e,1)}}var nn=()=>z.clone().multiplyScalar(jt).addScaledVector(It,Mt),rn=(e,t,n)=>Math.max(e,n*t),an=(()=>{let e=7,t=()=>(e=e*16807%2147483647)/2147483647,n=(e,t,n)=>z.clone().multiplyScalar(-Math.cos(e)).addScaledVector(It,Math.sin(e)*Math.cos(t)).addScaledVector(Ft,Math.sin(e)*Math.sin(t)).multiplyScalar(n),r=[{pos:n(14*R,.6,24e21),r:0x2086ac351052600000,spiral:!0,normal:Ft.clone().multiplyScalar(.97).addScaledVector(z,.24).normalize(),hue:.5}];for(let e=0;e<60;e++){let e=t()<.6,i=t()*2-1,a=t()*Math.PI*2;r.push({pos:n(t()*35*R,t()*Math.PI*2,6e21*33**t()),r:0x4563918244f400000*5**t(),spiral:e,normal:e?new w(Math.sqrt(1-i*i)*Math.cos(a),i,Math.sqrt(1-i*i)*Math.sin(a)):z.clone().add(new w(t()-.5,t()-.5,t()-.5).multiplyScalar(.6)).normalize(),hue:t()})}return r})(),on=`/moonbangoo/assets/earth-land-korea-i_Lq04Ra.png`,sn=`/moonbangoo/assets/earth-land-B2d1NBE3.png`,B=e=>`vec3(${St(e).map(e=>(e/255).toFixed(4)).join(`, `)})`,cn=`
  float hash(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
  }
  float noise(vec3 p) {
    vec3 i = floor(p), f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(hash(i), hash(i + vec3(1,0,0)), f.x), mix(hash(i + vec3(0,1,0)), hash(i + vec3(1,1,0)), f.x), f.y),
               mix(mix(hash(i + vec3(0,0,1)), hash(i + vec3(1,0,1)), f.x), mix(hash(i + vec3(0,1,1)), hash(i + vec3(1,1,1)), f.x), f.y), f.z);
  }
  float fbm(vec3 p, int octaves) {
    float sum = 0.0, amp = 0.5, norm = 0.0;
    for (int i = 0; i < 8; i++) {
      if (i >= octaves) break;
      sum += noise(p) * amp;
      norm += amp;
      p = p * 2.0 + 17.3;
      amp *= 0.5;
    }
    return sum / norm;
  }
  /** a > b 이면 거꾸로 (GLSL smoothstep은 a < b 만 정의돼 있다) */
  float ramp(float a, float b, float x) { return a < b ? smoothstep(a, b, x) : 1.0 - smoothstep(b, a, x); }
  /** 경도 차이를 -π~π로 */
  float lonDiff(vec3 p, float lon) {
    float d = atan(p.z, p.x) - lon;
    return d - 6.2831853 * floor(d / 6.2831853 + 0.5);
  }
  /** 구 위에 흩어 놓은 크레이터 (작은 것이 많게). x: 움푹한 바닥, y: 솟은 테두리 */
  vec2 craters(vec3 p, int count, float seed, float minR, float maxR) {
    vec2 c = vec2(0.0);
    for (int i = 0; i < 128; i++) {
      if (i >= count) break;
      float fi = float(i);
      float y = hash(vec3(fi, seed, 1.0)) * 2.0 - 1.0;
      float a = hash(vec3(fi, seed, 2.0)) * 6.2831853;
      float s = sqrt(1.0 - y * y);
      float r = minR + (maxR - minR) * pow(hash(vec3(fi, seed, 3.0)), 3.0);
      float t = length(p - vec3(s * cos(a), y, s * sin(a))) / r;
      c.x = max(c.x, 1.0 - smoothstep(0.7, 0.9, t));
      c.y = max(c.y, smoothstep(0.65, 0.92, t) * (1.0 - smoothstep(1.0, 1.4, t)));
    }
    return c;
  }
  /** 가스 행성 줄무늬: 위도를 따라 팔레트를 오가고, 경계는 난류로 휘어 있다. 반환 w: 가는 흰 결 */
  vec2 bands(vec3 p, float lat, float freq, float turb) {
    float t1 = fbm(p * vec3(4.0, 10.0, 4.0), 4) - 0.5;
    float t2 = fbm(vec3(p.x * 9.0 + t1 * 3.0, p.y * 24.0, p.z * 9.0 + t1 * 3.0), 3) - 0.5;
    return vec2(sin(lat * freq + (t1 * 2.2 + t2 * 0.8) * turb) * 0.5 + 0.5, max(0.0, t2) * 0.25 * turb);
  }
  vec3 palette(float t, vec3 c0, vec3 c1, vec3 c2, vec3 c3, vec3 c4) {
    t *= 4.0;
    return t < 1.0 ? mix(c0, c1, t) : t < 2.0 ? mix(c1, c2, t - 1.0) : t < 3.0 ? mix(c2, c3, t - 2.0) : mix(c3, c4, t - 3.0);
  }
`,ln=`
  #define BAKE_RGBA
  vec4 bakeRGBA(vec2 uv) {
    float lat = (uv.y - 0.5) * 3.14159265, lon = uv.x * 6.2831853;
    vec3 p = vec3(cos(lat) * cos(lon), sin(lat), cos(lat) * sin(lon));
    vec3 warp = vec3(fbm(p * 1.4 + vec3(7, 0, 0), 3), 0.0, fbm(p * 1.4 + vec3(0, 7, 0), 3)) - vec3(0.5, 0.0, 0.5);
    float alt = smoothstep(0.52, 0.7, fbm(p * 1.7 + warp * 1.6 + vec3(3, 0, 0), 7));
    float wet = fbm(p * 4.0 + vec3(20, 0, 0), 4);
    float a = abs(lat);
    float dry = smoothstep(0.12, 0.3, a) * (1.0 - smoothstep(0.55, 0.75, a)); // 아열대 사막대
    vec3 col = mix(${B(`#8cc363`)}, ${B(`#4b8a47`)}, smoothstep(0.45, 0.62, wet));
    col = mix(col, ${B(`#dfb878`)}, dry * ramp(0.5, 0.4, wet));
    col = mix(col, ${B(`#9b8466`)}, smoothstep(0.35, 0.75, alt));
    col = mix(col, ${B(`#f2f5f6`)}, smoothstep(0.85, 0.95, alt));
    float ice = abs(lat) + (fbm(p * 5.0, 3) - 0.5) * 0.4;
    col = mix(col, ${B(`#f2f5f6`)}, smoothstep(1.1, 1.16, ice));
    // 구름: 위도 방향으로 늘이고 비틀어 소용돌이처럼. 발사 지점(한반도) 위는 맑게
    float cw = fbm(p * 2.0 + vec3(11, 0, 0), 3) - 0.5;
    float cloud = smoothstep(0.55, 0.7, fbm(vec3(p.x * 3.0 + cw * 2.5, p.y * 6.0, p.z * 3.0 + cw * 2.5), 6));
    cloud *= smoothstep(0.08, 0.16, distance(p, vec3(${$t.toArray().map(e=>e.toFixed(6)).join(`, `)})));
    return vec4(col, cloud * 0.92);
  }`,un=`
  vec3 paint(vec3 p, float lat) {
    float sea = smoothstep(0.5, 0.56, fbm(p * 1.6 + vec3(4, 0, 0), 4)); // 어두운 바다
    vec3 col = mix(${B(`#d9d5cc`)}, ${B(`#8e8b85`)}, sea * 0.85);
    col = mix(col, ${B(`#b3afa7`)}, smoothstep(0.4, 0.65, fbm(p * 7.0, 4)) * 0.5);
    vec2 c = craters(p, 110, 21.0, 0.025, 0.32);
    col = mix(col, ${B(`#7d7a74`)}, c.x * 0.55);
    return mix(col, ${B(`#f1eee6`)}, c.y * 0.7);
  }`,dn=`
  vec3 paint(vec3 p, float lat) {
    float w = fbm(p * 2.0 + vec3(5, 0, 0), 3) - 0.5;
    vec3 col = mix(${B(`#e9a070`)}, ${B(`#cf6e42`)}, smoothstep(0.4, 0.55, fbm(p * 3.0 + vec3(w * 2.0, 0, w * 2.0), 5)));
    col = mix(col, ${B(`#8f4a30`)}, smoothstep(0.56, 0.64, fbm(p * 2.2 + vec3(w, 0, 0), 5)) * 0.8); // 어두운 무늬
    vec2 c = craters(p, 40, 22.0, 0.03, 0.18);
    col = mix(mix(col, ${B(`#a9532f`)}, c.x * 0.4), ${B(`#f2b98c`)}, c.y * 0.5);
    float cap = abs(lat) + (fbm(p * 6.0, 3) - 0.5) * 0.3;
    return mix(col, ${B(`#f7f8f6`)}, smoothstep(1.22, 1.27, cap));
  }`,fn=`
  vec3 paint(vec3 p, float lat) {
    vec2 b = bands(p, lat, 11.0, 1.0);
    vec3 col = palette(b.x, ${B(`#f2e6cc`)}, ${B(`#d3a777`)}, ${B(`#f6eedc`)}, ${B(`#a86c4a`)}, ${B(`#e9d2a8`)});
    col = mix(col, vec3(1.0), b.y);
    // 대적점: 붉은 타원 + 옅은 테두리, 안쪽은 소용돌이 결
    vec2 d = vec2(lonDiff(p, 0.6) * 1.1, (lat + 0.35) * 2.6);
    float spot = length(d);
    float swirl = sin(spot * 60.0 + atan(d.y, d.x) * 2.0) * 0.5 + 0.5;
    col = mix(col, ${B(`#f6ead2`)}, ramp(0.3, 0.24, spot) * 0.8);
    col = mix(col, mix(${B(`#c25a3a`)}, ${B(`#e0865a`)}, swirl * ramp(0.2, 0.06, spot)), ramp(0.24, 0.2, spot));
    // 작은 흰 타원들
    for (int k = 0; k < 4; k++) {
      float o = length(vec2(lonDiff(p, 2.0 + float(k) * 1.3) * 1.3, (lat + 0.62) * 3.5));
      col = mix(col, ${B(`#fbf6ec`)}, ramp(0.09, 0.06, o));
    }
    return col;
  }`,pn=`
  vec3 paint(vec3 p, float lat) {
    vec2 b = bands(p, lat, 8.0, 0.6);
    vec3 col = palette(b.x, ${B(`#f3e5bb`)}, ${B(`#dcc492`)}, ${B(`#f7edd0`)}, ${B(`#cdb17d`)}, ${B(`#ead7a8`)});
    col = mix(col, vec3(1.0), b.y);
    return mix(col, ${B(`#a9a88f`)}, smoothstep(1.05, 1.3, abs(lat)) * 0.6);
  }`,mn=`
  vec3 paint(vec3 p, float lat) {
    vec3 col = mix(${B(`#b5aca2`)}, ${B(`#857c73`)}, smoothstep(0.4, 0.62, fbm(p * 3.0, 5)));
    vec2 c = craters(p, 60, 23.0, 0.03, 0.2);
    return mix(mix(col, ${B(`#6f675f`)}, c.x * 0.5), ${B(`#d8d0c4`)}, c.y * 0.6);
  }`,hn=`
  vec3 paint(vec3 p, float lat) {
    vec2 b = bands(p, lat, 5.0, 1.6);
    return mix(mix(${B(`#d9b479`)}, ${B(`#f5e4bf`)}, b.x), vec3(1.0), b.y);
  }`,gn=`
  vec3 paint(vec3 p, float lat) {
    return mix(${B(`#9fd8e0`)}, ${B(`#c9eff1`)}, bands(p, lat, 6.0, 0.3).x * 0.6);
  }`,_n=`
  vec3 paint(vec3 p, float lat) {
    vec2 b = bands(p, lat, 7.0, 0.8);
    vec3 col = mix(${B(`#2f56b8`)}, ${B(`#5d8ae2`)}, b.x);
    float spot = length(vec2(lonDiff(p, 1.0) * 1.2, (lat + 0.4) * 3.0));
    return mix(mix(col, vec3(1.0), b.y), ${B(`#1c3580`)}, ramp(0.2, 0.14, spot));
  }`;function vn(e,t,n,r){let i=yn(t,n);return bn(e,i,r),i.texture}function yn(e,t){return new k(e,t,{colorSpace:he,generateMipmaps:!0,minFilter:p})}function bn(e,t,n,r={}){let i=new T({uniforms:r,vertexShader:`varying vec2 vUv; void main() { vUv = uv; gl_Position = vec4(position.xy, 0.0, 1.0); }`,fragmentShader:`
      varying vec2 vUv;
      ${cn}
      ${n}
      void main() {
        #ifdef BAKE_RGBA
        vec4 c = clamp(bakeRGBA(vUv), 0.0, 1.0);
        #else
        vec4 c = vec4(clamp(bakeColor(vUv), 0.0, 1.0), 1.0);
        #endif
        // 렌더 타깃엔 선형 색으로 쓴다 (sRGB 타깃이 저장할 때 다시 바꾼다)
        gl_FragColor = vec4(mix(c.rgb / 12.92, pow((c.rgb + 0.055) / 1.055, vec3(2.4)), step(0.04045, c.rgb)), c.a);
      }`}),o=new l(new b(2,2),i);o.frustumCulled=!1;let s=new a;e.setRenderTarget(t),e.render(o,s),e.setRenderTarget(null),o.geometry.dispose(),i.dispose()}var xn=`
  #define BAKE_RGBA
  uniform sampler2D uEarthMap, uLand;
  uniform mat3 uEarthRot;
  const vec2 KR0 = vec2(124.0, 31.0) * 0.01745329;
  const float KR_SPAN = 10.0 * 0.01745329;
  float hash2(vec2 p) {
    vec3 q = fract(vec3(p.xyx) * 0.1031);
    q += dot(q, q.yzx + 33.33);
    return fract((q.x + q.y) * q.z);
  }
  float noise2(vec2 p) {
    vec2 i = floor(p), f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash2(i), hash2(i + vec2(1, 0)), f.x), mix(hash2(i + vec2(0, 1)), hash2(i + vec2(1, 1)), f.x), f.y);
  }
  /** 윤곽 지도 한 칸(cell km)보다 잘고 지도 한 칸(px km)보다 굵은 결만 쌓은 잡음 (-0.5~0.5) */
  float detail(vec2 q, float cell, float px) {
    float sum = 0.0, amp = 0.25;
    for (int i = 0; i < 10; i++) {
      float fade = smoothstep(px * 1.5, px * 4.0, cell);
      if (fade <= 0.0) break;
      sum += (noise2(q / cell + float(i) * 17.0) - 0.5) * amp * fade;
      cell *= 0.5;
      amp *= 0.5;
    }
    return sum;
  }
  vec3 toSrgb(vec3 c) { return mix(c * 12.92, 1.055 * pow(c, vec3(1.0 / 2.4)) - 0.055, step(0.0031308, c)); }
  vec4 bakeRGBA(vec2 uv) {
    #ifdef KOREA
    vec2 ll = KR0 + uv * KR_SPAN;
    float cell = 1.1;
    #else
    vec2 ll = vec2(uv.x - 0.5, (uv.y - 0.5) * 0.5) * 6.2831853;
    float cell = 20.0;
    #endif
    vec3 e = vec3(cos(ll.y) * cos(ll.x), sin(ll.y), -cos(ll.y) * sin(ll.x));
    vec3 n = transpose(uEarthRot) * e;
    vec4 tex = textureLod(uEarthMap, fract(vec2(atan(e.z, e.x) / 6.2831853, ll.y / 3.14159265 + 0.5)), 0.0);
    vec2 q = vec2(n.x, -n.z) * 6371.0; // 발사 지점 기준 동·북 km
    float fine = detail(q, cell, length(fwidth(q)) * 0.7);
    return vec4(toSrgb(tex.rgb * (1.0 + fine * 0.8)), texture(uLand, uv).r + fine);
  }`;function Sn(e,t){let n=(n,r,i,a)=>{let o=yn(r,i),s={uEarthMap:{value:t},uLand:{value:null},uEarthRot:{value:en()}},c=En(n,()=>bn(e,o,a+xn,s));return s.uLand.value=c,{surface:o.texture,land:c}};return{earthMap:t,world:n(sn,2048,1024,``),korea:n(on,2048,2048,`#define KOREA
`)}}var Cn=(e,t,n)=>vn(e,n,n/2,`
    ${t}
    vec3 bakeColor(vec2 uv) {
      float lat = (uv.y - 0.5) * 3.14159265, lon = uv.x * 6.2831853;
      return paint(vec3(cos(lat) * cos(lon), sin(lat), cos(lat) * sin(lon)), lat);
    }`),wn=`
  /** n칸 격자에서 칸마다 확률 p로 둥근 점 하나 (반지름 size칸) */
  float dots(vec2 uv, float n, float p, float size, float seed) {
    vec2 g = uv * n, c = floor(g);
    if (hash(vec3(c, seed)) > p) return 0.0;
    vec2 o = vec2(hash(vec3(c, seed + 10.0)), hash(vec3(c, seed + 20.0))) * 0.6 + 0.2;
    return (1.0 - smoothstep(0.3, 1.0, length(g - c - o) / size)) * (0.5 + 0.5 * hash(vec3(c, seed + 30.0)));
  }
  vec3 bakeColor(vec2 uv) {
    vec2 p = uv * 2.0 - 1.0;
    float r = length(p) + 1e-4, a = atan(p.y, p.x), lr = log(r);
    float warp = (fbm(vec3(p * 1.8, 4.0), 3) - 0.5) * 0.8;
    float tu = a - 3.4 * lr + warp; // 풀어 놓은 각: 팔은 tu가 일정한 선
    vec2 cs = vec2(cos(tu), sin(tu));
    float streak = fbm(vec3(cs * 6.0, lr * 1.3), 5); // 팔 방향으로 길쭉한 결
    float fine = fbm(vec3(cs * 20.0, lr * 5.0), 4);
    float segment = smoothstep(0.3, 0.7, fbm(vec3(cs * 1.5, lr * 2.5 + 3.0), 3)); // 팔 밝기 마디
    float w = 0.5 + 0.5 * cos(2.0 * tu);
    float w2 = 0.5 + 0.5 * cos(2.0 * tu + 1.6);
    float armMask = smoothstep(0.1, 0.32, r);
    float disk = exp(-r * 2.6) * (1.0 - smoothstep(0.5, 0.97, r));
    float arms = (pow(w, 5.0) * (0.45 + 0.9 * segment) + pow(w2, 6.0) * 0.35 * (1.0 - segment)) * armMask;
    float ridge = pow(w, 16.0) * armMask * segment;
    float lane = pow(0.5 + 0.5 * cos(2.0 * (tu - 0.33)), 26.0) * smoothstep(0.3, 0.6, streak);
    float feather = pow(0.5 + 0.5 * cos(10.0 * (a - 1.1 * lr) + warp * 6.0), 6.0)
      * smoothstep(0.5, 0.7, fbm(vec3(p * 5.0, 11.0), 3)) * w * w;
    float dust = min(1.0, lane + feather * 0.7) * smoothstep(0.08, 0.22, r);
    float knots = dots(uv, 140.0, pow(w, 6.0) * armMask * 0.55, 0.32, 1.0);
    float pink = dots(uv, 110.0, ridge * 0.9, 0.42, 2.0);
    float core = exp(-r * r * 70.0) * 0.45 + exp(-r * r * 14.0) * 0.25;
    vec3 blue = vec3(0.62, 0.76, 1.0), warm = vec3(1.0, 0.84, 0.64);
    vec3 armCol = mix(blue, warm, smoothstep(0.45, 0.1, r) * 0.85);
    float inter = 0.12 + 0.22 * fbm(vec3(p * 4.0, 13.0), 4);
    vec3 col = disk * (warm * inter * 0.7 + armCol * (arms * (0.45 + streak) * 1.6 + ridge * fine * 0.9));
    col *= 1.0 - dust * 0.75;
    col += warm * core;
    col += vec3(0.85, 0.92, 1.0) * knots * disk * 2.2;
    col += vec3(1.0, 0.45, 0.7) * pink * disk * 1.8;
    return 1.0 - exp(-col * 1.6); // 밝은 곳을 부드럽게 눌러 가운데가 하얗게 타지 않게
  }`,Tn=`
  vec3 bakeColor(vec2 uv) {
    float r = length(uv * 2.0 - 1.0);
    return (1.0 - exp(-vec3(1.0, 0.85, 0.68) * (exp(-r * r * 9.0) * 0.9 + exp(-r * 5.0) * 0.3))) * (1.0 - smoothstep(0.8, 1.0, r));
  }`;function En(e,t){let n=new ie().load(e,t);return n.format=m,n}var V={earth:ln,moon:un,mercury:mn,venus:hn,mars:dn,jupiter:fn,saturn:pn,uranus:gn,neptune:_n,galaxy:wn,elliptical:Tn},Dn=new w(-.5,.6,.6).normalize();function H(e,t,n){let r=Math.sin(e*127.1+t*311.7+n*74.7)*43758.5453;return r-Math.floor(r)}function On(e,t){let n=Math.floor(e);return kn(H(n,t,0),H(n+1,t,0),O(0,1,e-n))}var kn=(e,t,n)=>e+(t-e)*n,An=(e,t,n)=>[e[0]+(t[0]-e[0])*n,e[1]+(t[1]-e[1])*n,e[2]+(t[2]-e[2])*n];function jn(e,t){return new T({uniforms:{map:{value:e},uSun:{value:Dn},uRim:{value:new g(t)}},vertexShader:`
      varying vec3 vNormal, vView;
      varying vec2 vUv;
      void main() {
        vec4 world = modelMatrix * vec4(position, 1.0);
        vNormal = normalize(mat3(modelMatrix) * normal);
        vView = normalize(cameraPosition - world.xyz);
        vUv = uv;
        gl_Position = projectionMatrix * viewMatrix * world;
      }`,fragmentShader:`
      uniform sampler2D map;
      uniform vec3 uSun, uRim;
      varying vec3 vNormal, vView;
      varying vec2 vUv;
      void main() {
        vec3 n = normalize(vNormal);
        float sun = dot(n, uSun);
        float light = 0.2 + 0.8 * smoothstep(-0.05, 0.25, sun);
        // 밤낮 경계에 따뜻한 노을 띠
        vec3 dusk = vec3(1.0, 0.55, 0.3) * (smoothstep(-0.12, 0.05, sun) - smoothstep(0.05, 0.3, sun)) * 0.25;
        float rim = pow(1.0 - max(dot(n, normalize(vView)), 0.0), 3.0);
        vec3 col = texture2D(map, vUv).rgb * (light + dusk) + uRim * rim * 0.8;
        gl_FragColor = vec4(col, 1.0);
        #include <colorspace_fragment>
      }`})}var Mn=new te(1,64,32),Nn=(e,t)=>new l(Mn,jn(e,t));function Pn(e){let t=345.6/2.3,n=St(`#ecdcb4`),r=St(`#b39f7c`),i=[];for(let e=t;e<256;e+=1){let a=(e-t)/105.7391304347826,o=On(a*40,1)*.6+On(a*140,2)*.4,s=a>.6&&a<.66?.08:a>.9&&a<.91?.3:1,c=(.25+.65*o)*O(0,.08,a)*s;i.push({r:e,col:An(r,n,O(.15,.55,a)*(.6+.4*o)),a:c})}let a=Ct(512,512,(e,n)=>{let r=Math.hypot(e-256,n-256),a=0,o=[0,0,0],s=Math.max(0,Math.ceil(r-1-t));for(let e=s;e<i.length&&i[e].r<r+1;e++){let t=i[e],n=t.a*Math.max(0,1-Math.abs(r-t.r)),s=n+a*(1-n);s>0&&(o=An(o,t.col,n/s)),a=s}return[...o,a]}),o=new l(new S(e*1.35,e*2.3,96),new T({uniforms:{map:{value:a}},vertexShader:`varying vec2 vUv; void main() { vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }`,fragmentShader:`uniform sampler2D map; varying vec2 vUv; void main() { gl_FragColor = texture2D(map, vUv); #include <colorspace_fragment>
 }`,transparent:!0,side:2,depthWrite:!1}));return o.rotation.x=-Math.PI/2+.45,o}var Fn=[[0,`#f8f3e6`,`#f8f3e6`],[4,`#f8f8f7`,`#f0f3f2`],[8,`#dcedf5`,`#a9d3ef`],[16,`#c2e1f3`,`#5e9fdc`],[21,`#5d7fbd`,`#1c3670`],[26,`#141b33`,`#04060d`],[57,`#141b33`,`#04060d`],[74,`#1c1233`,`#07040f`],[106,`#07060d`,`#020206`]];function In(e,t){let n=new T({uniforms:{uHorizon:{value:new g},uZenith:{value:new g},uStars:{value:0},uBand:{value:0},uTime:{value:0},uInvProj:{value:e.projectionMatrixInverse},uCamWorld:{value:e.matrixWorld},uEarthMap:{value:t.earthMap},uLand:{value:t.world.land},uLandKr:{value:t.korea.land},uSurface:{value:t.world.surface},uSurfaceKr:{value:t.korea.surface},uEarthRot:{value:en()},uEarthC:{value:new w},uEarthR:{value:1},uEarthQ:{value:1},uMeter:{value:1},uFar:{value:1},uHaze:{value:1},uAir:{value:0},uSun:{value:Dn}},vertexShader:`
      uniform mat4 uInvProj, uCamWorld;
      varying vec3 vDir;
      void main() {
        vec4 p = uInvProj * vec4(position.xy, 1.0, 1.0);
        vDir = (uCamWorld * vec4(p.xyz / p.w, 0.0)).xyz;
        gl_Position = vec4(position.xy, 1.0, 1.0);
      }`,fragmentShader:`
      uniform vec3 uHorizon, uZenith, uEarthC, uSun;
      uniform float uStars, uBand, uTime, uEarthR, uEarthQ, uMeter, uFar, uHaze, uAir;
      uniform sampler2D uEarthMap, uLand, uLandKr, uSurface, uSurfaceKr;
      uniform mat3 uEarthRot;
      varying vec3 vDir;
      // 정밀 육지 윤곽(earth-land-korea.png)의 범위: 동경 124~134°, 북위 31~41°
      const vec2 KR0 = vec2(124.0, 31.0) * 0.01745329;
      const float KR_SPAN = 10.0 * 0.01745329;
      float hash(vec3 p) { return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453); }
      float noise(vec3 p) {
        vec3 i = floor(p), f = fract(p);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(mix(hash(i), hash(i + vec3(1,0,0)), f.x), mix(hash(i + vec3(0,1,0)), hash(i + vec3(1,1,0)), f.x), f.y),
                   mix(mix(hash(i + vec3(0,0,1)), hash(i + vec3(1,0,1)), f.x), mix(hash(i + vec3(0,1,1)), hash(i + vec3(1,1,1)), f.x), f.y), f.z);
      }
      // 방향을 격자로 나눠 칸마다 별 하나를 둘지 정한다
      float stars(vec3 d, float scale, float density) {
        vec3 p = d * scale, c = floor(p);
        if (hash(c) > density) return 0.0;
        vec3 o = vec3(hash(c + 1.7), hash(c + 3.1), hash(c + 5.9)) - 0.5;
        float r = length(fract(p) - 0.5 - o * 0.6);
        float twinkle = 0.75 + 0.25 * sin(uTime * (1.5 + hash(c + 9.0) * 3.0) + hash(c) * 40.0);
        return smoothstep(0.12, 0.0, r) * twinkle * (0.4 + 0.6 * hash(c + 2.3));
      }
      /** 지구 표면 색: 구워 둔 표면 지도 + 행성 재질과 같은 명암 + 멀수록 옅은 하늘색 대기, 낮은 고도에선 지평선 색 안개 */
      vec3 earth(vec3 d, float t) {
        vec3 n = normalize(d * t - uEarthC);
        vec3 e = uEarthRot * n;
        vec2 uv = vec2(atan(e.z, e.x) / 6.2831853, asin(clamp(e.y, -1.0, 1.0)) / 3.14159265 + 0.5);
        // 경도가 한 바퀴 넘어가는 이음매에서 밉맵이 튀지 않게 기울기를 감아서 넘긴다
        vec2 dx = dFdx(uv), dy = dFdy(uv);
        dx.x -= floor(dx.x + 0.5);
        dy.x -= floor(dy.x + 0.5);
        float sun = dot(n, uSun);
        float light = 0.2 + 0.8 * smoothstep(-0.05, 0.25, sun);
        vec3 dusk = vec3(1.0, 0.55, 0.3) * (smoothstep(-0.12, 0.05, sun) - smoothstep(0.05, 0.3, sun)) * 0.25;
        float rim = pow(1.0 - max(dot(n, -d), 0.0), 3.0);
        // 포항 주변은 정밀 지도, 나머지는 세계 지도. 육지·바다는 화면 픽셀마다 갈라 해안선이 또렷하다
        float lon = atan(-e.z, e.x), lat = asin(clamp(e.y, -1.0, 1.0));
        vec2 gw = vec2(lon / 6.2831853 + 0.5, lat / 3.14159265 + 0.5);
        vec2 gdx = dFdx(gw), gdy = dFdy(gw);
        gdx.x -= floor(gdx.x + 0.5);
        gdy.x -= floor(gdy.x + 0.5);
        vec2 kr = (vec2(lon, lat) - KR0) / KR_SPAN;
        float inKr = smoothstep(0.0, 0.15, min(min(kr.x, kr.y), 1.0 - max(kr.x, kr.y))); // 두 지도의 결이 달라 넓게 섞는다
        vec4 land = mix(textureGrad(uSurface, fract(gw), gdx, gdy), texture(uSurfaceKr, kr), inKr);
        float coast = mix(textureGrad(uLand, fract(gw), gdx * 16.0, gdy * 16.0).r, texture(uLandKr, kr, 4.0).r, inKr); // 번진 윤곽: 해안 가까울수록 얕게
        float m = land.a;
        float aa = fwidth(m) * 0.7 + 1e-3;
        vec3 water = mix(${B(`#123f8c`)}, ${B(`#1f5fb3`)}, smoothstep(0.0, 0.3, coast));
        water = mix(water, ${B(`#4a9fd6`)}, smoothstep(0.3, 0.5, m) * smoothstep(0.02, 0.1, coast)); // 먼 바다엔 얕은 빛이 안 뜨게
        vec3 ground = mix(${B(`#ecdcaa`)}, land.rgb, smoothstep(0.5, 0.62, m));
        vec3 surface = mix(mix(water, ground, smoothstep(0.5 - aa, 0.5 + aa, m)), ${B(`#f7f8f6`)}, textureGrad(uEarthMap, fract(uv), dx, dy).a);
        vec3 col = surface * (light + dusk) + vec3(0.1, 0.27, 0.55) * rim * 0.8 * uAir;
        float distant = 1.0 - exp(-t * uMeter / 6e5);
        col = mix(col, vec3(0.42, 0.62, 0.85), distant * 0.8 * light * uFar);
        return mix(col, uHorizon, uHaze);
      }
      void main() {
        vec3 d = normalize(vDir);
        vec3 col = mix(uHorizon, uZenith, smoothstep(-0.15, 0.6, d.y));
        if (uStars > 0.0) col += uStars * (stars(d, 110.0, 0.05) * vec3(1.0, 0.95, 0.85) + stars(d, 240.0, 0.08) * 0.6);
        if (uBand > 0.0) {
          // 은하 안에서 보이는 별무리 띠
          float lat = dot(d, normalize(vec3(0.35, 0.25, 0.9)));
          float n = noise(d * 5.0) * 0.6 + noise(d * 13.0) * 0.4;
          float band = exp(-lat * lat * 18.0) * smoothstep(0.3, 0.8, n);
          col += uBand * band * mix(vec3(0.45, 0.35, 0.9), vec3(1.0, 0.6, 0.75), n) * 0.6;
          col += uBand * stars(d, 400.0, 0.25 * exp(-lat * lat * 10.0)) * 0.8;
        }
        float b = dot(d, uEarthC);
        float disc = b * b - uEarthQ;
        // 낮은 고도에선 땅이 지평선 색 안개에 완전히 묻히므로 무거운 지구 계산을 건너뛴다
        if (b > 0.0 && disc > 0.0) col = uHaze < 1.0 ? earth(d, uEarthQ / (b + sqrt(disc))) : uHorizon; // 가까운 교점 (뺄셈 없이)
        else if (b > 0.0 && uAir > 0.0) {
          // 우주에서 본 지구 윤곽 바깥의 얇은 대기 띠
          float h = sqrt(max(dot(uEarthC, uEarthC) - b * b, 0.0)) - uEarthR;
          float lit = 0.25 + 0.75 * smoothstep(-0.3, 0.4, dot(normalize(d * b - uEarthC), uSun));
          col += vec3(0.44, 0.71, 1.0) * exp(-max(h, 0.0) / (0.012 * uEarthR)) * lit * uAir * 0.8;
        }
        gl_FragColor = vec4(col, 1.0);
        #include <colorspace_fragment>
      }`,depthWrite:!1,toneMapped:!1}),r=new l(new b(2,2),n);return r.frustumCulled=!1,r.renderOrder=10,{mesh:r,uniforms:n.uniforms}}function Ln(e){let t=new T({uniforms:{map:{value:e},uOpacity:{value:0}},vertexShader:`varying vec2 vUv; void main() { vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }`,fragmentShader:`
      uniform sampler2D map;
      uniform float uOpacity;
      varying vec2 vUv;
      float hash(vec2 p) {
        vec3 q = fract(vec3(p.xyx) * 0.1031);
        q += dot(q, q.yzx + 33.33);
        return fract((q.x + q.y) * q.z);
      }
      /** 한 변에 n칸, 칸마다 확률 density로 별 하나. px: 화면 1px에 해당하는 uv 길이 */
      float stars(float n, float density, float px) {
        float cellPx = 1.0 / (n * px);
        if (cellPx < 3.0) return 0.0;
        vec2 g = vUv * n, c = floor(g);
        float h = hash(c);
        if (h > density) return 0.0;
        vec2 o = vec2(hash(c + 7.1), hash(c + 3.3)) * 0.6 + 0.2;
        float d = length(fract(g) - o) * cellPx; // px 단위 거리
        return smoothstep(1.4, 0.3, d) * (0.4 + 0.6 * hash(c + 1.9)) * smoothstep(3.0, 8.0, cellPx);
      }
      void main() {
        vec3 base = texture2D(map, vUv).rgb;
        float px = max(length(fwidth(vUv)) * 0.7, 1e-7);
        float lum = dot(base, vec3(0.3, 0.5, 0.2));
        float density = clamp(lum * 0.6, 0.01, 0.25) * (1.0 - smoothstep(0.85, 1.0, length(vUv * 2.0 - 1.0)));
        float s = stars(256.0, density, px) + stars(768.0, density, px) + stars(2304.0, density, px) + stars(6912.0, density, px);
        vec3 tint = mix(vec3(0.85, 0.9, 1.0), vec3(1.0, 0.92, 0.8), smoothstep(0.2, 0.6, lum));
        gl_FragColor = vec4((base + tint * min(s, 1.5) * 0.8) * uOpacity, 1.0);
        #include <colorspace_fragment>
      }`,transparent:!0,blending:2,depthWrite:!1,side:2});return{mesh:new l(new b(2,2),t),uniforms:t.uniforms}}function Rn(e,t,n){return new l(zn,new T({uniforms:{map:{value:e},uTint:{value:t},uOpacity:n},vertexShader:`varying vec2 vUv; void main() { vUv = uv; gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0); }`,fragmentShader:`
      uniform sampler2D map;
      uniform vec3 uTint;
      uniform float uOpacity;
      varying vec2 vUv;
      void main() {
        gl_FragColor = vec4(texture2D(map, vUv).rgb * uTint * uOpacity, 1.0);
        #include <colorspace_fragment>
      }`,transparent:!0,blending:2,depthWrite:!1,side:2}))}var zn=new b(2,2);function Bn(e){let t=Array.from({length:40},(t,n)=>{let r=H(e,n,1);return{x:128+(r-.5)*190,y:64+(H(e,n,2)-.5)*40*Math.sin(r*Math.PI),r:18+H(e,n,3)*30}});return Ct(256,128,(e,n)=>{let r=0;for(let i of t){let t=wt(Math.hypot(e-i.x,n-i.y)/i.r,[[0,.22],[1,0]]);r=t+r*(1-t)}return[255,255,255,r]})}function Vn(){return Ct(32,32,(e,t)=>[255,255,255,wt(Math.hypot(e-16,t-16)/16,[[0,1],[1,0]])])}var Hn=220,Un=30;function U(e,t,n,r,i){e.position.subVectors(t,r).multiplyScalar(i),e.scale.setScalar(n*i)}var Wn=256;function Gn(e,t){let n=new f,i=new pe,a=new v(e.fov,1,1,1e7),o=In(a,Sn(t,vn(t,2048,1024,V.earth))),l=Nn(Cn(t,V.moon,1024),`#ffffff`),d={mercury:[V.mercury,`#e8e0d6`],venus:[V.venus,`#fff0d0`],mars:[V.mars,`#ffb38a`],jupiter:[V.jupiter,`#ffe2b8`],saturn:[V.saturn,`#fff0c8`],uranus:[V.uranus,`#d8fbff`],neptune:[V.neptune,`#9cc0ff`]},p=Kt.map(e=>{let[n,r]=d[e.name];return{...e,pos:qt(e.orbit,e.angle),mesh:Nn(Cn(t,n,512),r)}});p.find(e=>e.name===`saturn`).mesh.add(Pn(1));let m=Ht(),h=new D(new A({map:Vn(),color:`#ffe6a8`,blending:2,transparent:!0,depthWrite:!1})),_=new ye().setFromPoints(Array.from({length:Wn},(e,t)=>new w(Math.cos(t/Wn*Math.PI*2),0,Math.sin(t/Wn*Math.PI*2)))),y=new se({color:`#9fb4e0`,transparent:!0,depthWrite:!1,opacity:0}),b=[kt,...Kt.map(e=>e.orbit)].map(e=>({r:e,line:new u(_,y)}));for(let e of b)e.line.quaternion.setFromUnitVectors(new w(0,1,0),Wt());let ee=nn(),te=Ut(),ne=new w(0,0,1),re=new s().setFromUnitVectors(ne,Gt()),ie=new s,x=vn(t,2048,2048,V.galaxy),S=Ln(x),C=vn(t,128,128,V.elliptical),ae={value:0},oe=an.map(e=>{let t=e.spiral?new g().lerpColors(new g(`#c9d6ff`),new g(`#fff0dc`),e.hue):new g(`#fff4e6`),n=Rn(e.spiral?x:C,t,ae);return n.quaternion.setFromUnitVectors(ne,e.normal).multiply(new s().setFromAxisAngle(ne,e.hue*Math.PI*2)),{...e,mesh:n}});i.add(o.mesh,l,h,S.mesh,...p.map(e=>e.mesh),...b.map(e=>e.line),...oe.map(e=>e.mesh));let T=[],ce=[0,1,2].map(e=>new A({map:Bn(e),fog:!1,depthWrite:!1,transparent:!0}));for(let e=0;e<42;e++){let t=0,r=0;for(let n=0;n===0||Math.abs(t)<2.6&&r>-4;n++)t=(H(e,1,n)-.5)*26,r=-18+H(e,2,n)*20;let i=new D(ce[e%3]),a=4+H(e,3,0)*6;i.scale.set(a,a/2.5,1),i.position.set(t,10+H(e,4,0)*5,r),T.push(i),n.add(i)}let E=new Float32Array(660);for(let e=0;e<Hn;e++)E[e*3]=(H(e,7,0)-.5)*22,E[e*3+1]=H(e,8,0)*Un,E[e*3+2]=-16+H(e,9,0)*20;let le=new ye;le.setAttribute(`position`,new r(E.slice(),3));let ue=new de({map:Vn(),size:.09,transparent:!0,depthWrite:!1,fog:!1,opacity:0}),k=new c(le,ue);k.frustumCulled=!1,n.add(k);let fe=new g,me=new g,he=new g,ge=new w;return{group:n,cosmos:i,cosmosCamera:a,horizon:fe,update(t,n){let r=0;for(;r<Fn.length-2&&t>Fn[r+1][0];)r++;let[i,s,c]=Fn[r],[u,d,f]=Fn[r+1],g=O(i,u,t);fe.set(s).lerp(he.set(d),g),me.set(c).lerp(he.set(f),g);let _=o.uniforms;_.uHorizon.value.copy(fe),_.uZenith.value.copy(me),_.uTime.value=n,a.fov=e.fov,a.aspect=e.aspect,a.updateProjectionMatrix(),a.quaternion.copy(e.quaternion);let v=we(t),x=Jt(v),{cam:C,k:w}=tn(v),se=Yt(v),D=Vt*se*O(9,10,x);ge.copy(C).negate();let k=ge.length(),de=rn(Et,k,Vt*se);_.uEarthC.value.copy(ge).multiplyScalar(w),_.uEarthR.value=de*w,_.uEarthQ.value=(k-de)*(k+de)*w*w,_.uMeter.value=1/w,_.uFar.value=1-O(5.6,6.6,x),_.uHaze.value=1-O(3.8,5,x),_.uAir.value=O(4.7,5.4,x),_.uStars.value=O(19,27,t)*(1-.65*O(20.2,21,x)),_.uBand.value=O(12.15,12.65,x)*(1-O(19,20.2,x)),U(l,ee,Dt,C,w),l.rotation.y=n*.05;for(let e of p)U(e.mesh,e.pos,rn(e.r,e.pos.distanceTo(C),D*e.boost),C,w),e.mesh.rotation.y=n*.05;U(h,m,rn(Ot,m.distanceTo(C),.012*(1-O(13.5,15,x)))*5,C,w),y.opacity=O(9.8,10.6,x)*se*.45;for(let e of b)e.line.visible=y.opacity>0,U(e.line,m,e.r,C,w);U(S.mesh,te,At,C,w),S.mesh.quaternion.copy(re).multiply(ie.setFromAxisAngle(ne,n*.01)),S.uniforms.uOpacity.value=O(19,20.2,x),S.mesh.visible=S.uniforms.uOpacity.value>0,ae.value=O(20.2,20.9,x);for(let e of oe)e.mesh.visible=ae.value>0,U(e.mesh,e.pos,e.r,C,w),e.spiral||(e.mesh.scale.y*=.6);let pe=1-O(17,21,t);for(let e of ce)e.opacity=pe;for(let e of T)e.visible=pe>0;let A=e.position.y-Un/2,_e=le.getAttribute(`position`);for(let e=0;e<Hn;e++){let t=E[e*3+1];_e.setY(e,A+((t-A)%Un+Un)%Un)}_e.needsUpdate=!0,ue.opacity=O(3,9,t)*.45+O(20,28,t)*.4}}}var Kn=[[3150,.32],[4870,.22],[7130,.14],[9620,.09]],qn=class{ctx=null;master=null;noise=null;wind=null;unlock(){if(typeof AudioContext<`u`){if(!this.ctx)try{let e=new AudioContext;this.ctx=e,this.master=e.createGain(),this.master.gain.value=.7,this.master.connect(e.destination),this.noise=e.createBuffer(1,e.sampleRate,e.sampleRate);let t=this.noise.getChannelData(0);for(let e=0;e<t.length;e++)t[e]=Math.random()*2-1;let n=e.createBufferSource();n.buffer=this.noise,n.loop=!0;let r=e.createBiquadFilter();r.type=`bandpass`,r.Q.value=1.4;let i=e.createGain();i.gain.value=0,n.connect(r).connect(i).connect(this.master),n.start(),this.wind={gain:i,filter:r}}catch{return}this.ctx.resume().catch(()=>{})}}get live(){return this.ctx?.state===`running`?this.ctx:null}setWind(e){let t=this.live;if(!t||!this.wind)return;let n=t.currentTime;this.wind.gain.gain.setTargetAtTime(e*e*.22,n,.06),this.wind.filter.frequency.setTargetAtTime(380+e*1600,n,.06)}clink(e,t=1){let n=this.live;if(!n)return;let r=n.currentTime,i=.97+Math.random()*.06;for(let[a,o]of Kn){let s=n.createOscillator();s.frequency.value=a*t*i;let c=n.createGain();c.gain.setValueAtTime(.12*e,r),c.gain.exponentialRampToValueAtTime(1e-4,r+o*(.5+e)),s.connect(c).connect(this.master),s.start(r),s.stop(r+o*1.5+.05)}this.burst(r,.015,.5*e,3200)}tick(e){let t=this.live;t&&this.burst(t.currentTime,.008,.08+.3*e,2600)}burst(e,t,n,r){let i=this.ctx,a=i.createBufferSource();a.buffer=this.noise;let o=i.createBiquadFilter();o.type=`bandpass`,o.frequency.value=r,o.Q.value=2;let s=i.createGain();s.gain.setValueAtTime(n,e),s.gain.exponentialRampToValueAtTime(1e-4,e+t),a.connect(o).connect(s).connect(this.master),a.start(e,Math.random()*.5,t+.01)}},Jn=Te(500),Yn=.6,Xn=class{climb=0;elapsed=null;get active(){return this.elapsed!==null}get waitingOpacity(){return this.elapsed===null?1:O(0,Yn,this.elapsed)}get thrownOpacity(){return this.elapsed===null?0:1-this.waitingOpacity}step(e,t){if(this.elapsed!==null)return this.elapsed+=t,this.elapsed>=Yn&&(e.reset(),this.climb=0,this.elapsed=null),[];let n=[];for(let r=t;r>1e-8;r-=1/240){let t=e.step(Math.min(r,1/240));if(n.push(...t),this.climb=Math.max(this.climb,e.pos.y-P/2),this.climb<Jn&&t.some(e=>e.type===`bounce`||e.type===`settle`)){this.elapsed=0;break}}return n}},Zn=class{elapsed=1/0;approach=0;strike(){this.elapsed=0,this.approach=0}reset(){this.elapsed=1/0,this.approach=0}step(e,t,n){this.elapsed+=e;let r=this.elapsed,i=r<.95,a=i?1-O(.45,.95,r):0,o=O(.12,.3,r),s=!i&&n?1-O(.5,4,t):0;this.approach+=(s-this.approach)*(1-Math.exp(-e*18));let c=i||this.approach<.001?0:this.approach;return{ready:c,contact:i?a*(1-o):0,release:i?a*o:0,retreat:i?O(.32,.95,r):1-c,flash:i?1-O(.03,.2,r):0,ripple:i?1-O(0,.7,r):0}}},Qn=new URL(`/moonbangoo/assets/divine-hand-jOCO9jzU.webp`,``+import.meta.url).href,$n=2.55;async function er(){let e=await new ie().loadAsync(Qn);e.colorSpace=he;let t=new f,n=new Zn,r=[0,1,2].map(n=>{let r=e.clone();r.repeat.set(1/3,1),r.offset.set(n/3,0);let i=new D(new A({map:r,transparent:!0,depthWrite:!1,depthTest:!1,toneMapped:!1,fog:!1}));return i.center.set(.3,.11),i.scale.set($n,$n,1),i.renderOrder=20+n,t.add(i),i}),i=Ct(64,64,(e,t)=>[255,242,195,wt(Math.hypot(e-32,t-32)/32,[[0,1],[.18,.7],[1,0]])]),a=new D(new A({map:i,transparent:!0,depthWrite:!1,depthTest:!1,blending:2,toneMapped:!1,fog:!1}));a.renderOrder=24,t.add(a);let o=new Float32Array(54),s=new ye;s.setAttribute(`position`,new ge(o,3));let l=new c(s,new de({map:i,color:`#ffe6a8`,size:.09,transparent:!0,depthWrite:!1,depthTest:!1,blending:2,toneMapped:!1,fog:!1}));l.renderOrder=23,t.add(l);let u=new w,d=new w,p=new w,m=new w(0,F+N*.65,0);return t.visible=!1,{group:t,motion:n,update(e,i,c,f){let h=n.step(e,F-i,c>0);if(t.visible=h.ready+h.contact+h.release>0,!t.visible)return h;d.setFromMatrixColumn(f.matrixWorld,0),p.setFromMatrixColumn(f.matrixWorld,1);let g=[h.ready,h.contact,h.release];r.forEach((e,t)=>{e.material.opacity=g[t]*.94,e.visible=g[t]>.001,e.position.copy(m).addScaledVector(d,h.retreat*.38).addScaledVector(p,h.retreat*.45)}),a.position.copy(m),a.scale.setScalar(.55+(1-h.flash)*.2),a.material.opacity=h.flash,l.material.opacity=h.ripple*.8;for(let e=0;e<18;e++){let t=e*2.39996,n=(1-h.ripple)*(.4+e%4*.15);u.copy(m).addScaledVector(d,Math.cos(t)*n).addScaledVector(p,Math.sin(t)*n*.45-(1-h.ripple)**2*.25).toArray(o,e*3)}return s.attributes.position.needsUpdate=!0,h}}}var tr=`#f3ecdc`,nr=.45,rr=1.1,ir=.3,ar=1,or=3,sr=100,cr=.6,lr=.015,ur={front:`앞면`,back:`뒷면`},dr=Math.PI/180,W=new Xn,fr=e=>(40-26*O(1.5,7,e))*dr,pr=24,mr=e=>1-O(18,28,e),hr=(e,t)=>O(9,16,-t)*O(8,14,e)*(1-O(30,40,e)),G=document.querySelector(`#stage`),gr=document.querySelector(`#result`),_r=document.querySelector(`#hint`),vr=document.querySelector(`#altitude`),yr=document.querySelector(`#gauge`),K=new ee({canvas:G,antialias:!0,powerPreference:`high-performance`});K.setPixelRatio(Math.min(E.maxPixelRatio,window.devicePixelRatio)),K.toneMapping=7,K.autoClear=!1;var q=new pe,br=new n(`#ffffff`,9,26);q.fog=br;var J=new v(30,1,.1,400),Y=Gn(J,K);q.add(Y.group);var xr=8,Sr=new h({color:tr,toneMapped:!1,transparent:!0}),Cr=new l(new b(80,80),Sr);Cr.rotation.x=-Math.PI/2,q.add(Cr);var wr=new h({map:jr(),transparent:!0,depthWrite:!1,toneMapped:!1}),Tr=new l(new b(1,1),wr);Tr.rotation.x=-Math.PI/2,Tr.position.y=.002,q.add(Tr);var Er=new h({color:`#ffe4a0`,transparent:!0,opacity:0,depthWrite:!1,side:2,fog:!1,blending:2,toneMapped:!1}),X=new l(new S(.94,1,128),Er);X.rotation.x=-Math.PI/2,X.position.y=F+N*.65,X.visible=!1,q.add(X);var Z=0,Dr=null,Q=new tt,$=new qn,Or=new ae(0,2.4,1),kr=new ae(fr(0),.8,1),Ar=e=>{try{navigator.vibrate?.(e)}catch{}};function jr(){return Ct(128,128,(e,t)=>[0,0,0,wt(Math.hypot(e-64,t-64)/64,[[0,.42],[.45,.22],[1,0]])])}function Mr(){let e=[[0,255,245,210,1],[.25,255,170,60,.9],[.7,255,90,20,.35],[1,255,60,10,0]],t=document.createElement(`canvas`);t.width=256,t.height=640;let n=t.getContext(`2d`),r=n.createImageData(256,640);for(let t=0;t<640;t++){let n=1-(t+.5)/640,i=e.findIndex(e=>e[0]>=n),[a,...o]=e[i-1],[s,...c]=e[i],l=(n-a)/(s-a),u=o.map((e,t)=>e+(c[t]-e)*l),d=.46*Math.sqrt(Math.sin(Math.PI*n**.6));for(let e=0;e<256;e++){let n=Math.abs((e+.5)/256-.5)/Math.max(d,.001),i=n<1?(1-n*n)**2:0,a=(t*256+e)*4;r.data[a]=u[0],r.data[a+1]=u[1],r.data[a+2]=u[2],r.data[a+3]=u[3]*i*255}}return n.putImageData(r,0,0),new ne(t)}function Nr(){let e=G.clientWidth,t=G.clientHeight;K.setSize(e,t,!1),J.aspect=e/t,J.updateProjectionMatrix();let n=2*Math.tan(J.fov/2*Math.PI/180);xr=Math.max(2*N/(.42*n*J.aspect),2*N/(.27*n))}new ResizeObserver(Nr).observe(G);var Pr=0;function Fr(e){e&&(_r.textContent=e),_r.classList.toggle(`is-shown`,!!e)}function Ir(e){e&&(gr.textContent=ur[e]),gr.classList.toggle(`is-shown`,!!e)}var Lr=new Set;function Rr(e){if(e.type===`ceiling`)Z=1,Dr?.motion.strike(),$.clink(.16+e.strength*.12,.65),Ar(18);else if(e.type===`bounce`)$.clink(.3+.7*e.strength),Ar(10+30*e.strength);else if(e.type===`tick`)$.tick(e.strength),e.strength>.3&&Ar(5);else if(e.type===`rest`){$.clink(.12,.8),Ar(20);let t=W.climb>=Jn;Ir(t?e.face:null),Fr(t?`위로 튕겨서 다시 던지기`:`500m를 넘겨야 결과가 나와요. 다시 던져 보세요`),W.climb=0,vr.classList.remove(`is-shown`)}}var zr=new Map,Br=new Map;G.addEventListener(`pointerdown`,e=>{W.active||($.unlock(),G.setPointerCapture(e.pointerId),zr.set(e.pointerId,[{x:e.clientX,y:e.clientY,t:e.timeStamp}]))}),G.addEventListener(`pointermove`,e=>{let t=zr.get(e.pointerId);if(!t||W.active)return;let n=t[t.length-1],r=G.clientHeight,i=(e.clientX-n.x)/r,a=-(e.clientY-n.y)/r;if(!W.active&&Q.canSpin){Q.spin(i,a),Pr+=Math.hypot(i,a);let t=Br.get(e.pointerId)??{rub:0,dx:i,up:a};i*t.dx+a*t.up<0&&(t.rub=0),(i||a)&&Object.assign(t,{dx:i,up:a});let n=t.rub;t.rub+=Math.hypot(i,a),Br.set(e.pointerId,t),n<lr&&t.rub>=lr&&($.clink(.3,1.25),Ar(5)),Pr>cr&&_r.classList.contains(`is-shown`)&&Fr(``)}for(t.push({x:e.clientX,y:e.clientY,t:e.timeStamp});t.length>2&&e.timeStamp-t[1].t>sr;)t.shift()}),G.addEventListener(`pointerup`,e=>{$.unlock();let t=zr.get(e.pointerId);if(zr.delete(e.pointerId),Br.delete(e.pointerId),!t||W.active)return;let n=t.find(t=>e.timeStamp-t.t<=sr)??t[t.length-1],r=Math.max(1,e.timeStamp-n.t)/1e3,i=G.clientHeight;Q.launch((e.clientX-n.x)/i/r,-(e.clientY-n.y)/i/r)&&($.clink(.35,1.25),Ar(8),W.climb=0,Ir(null),Fr(Pr<cr?`떠 있는 동안 화면을 스와이프하여 더 돌려 보세요`:``),Dr?.motion.reset(),Z=0,Lr=new Set,vr.classList.add(`is-shown`))}),G.addEventListener(`pointercancel`,e=>{zr.delete(e.pointerId),Br.delete(e.pointerId)});var Vr=new w,Hr=new s;function Ur(e){let t=e.map(e=>Object.assign(e.clone(),{transparent:!0,depthWrite:!1,alphaHash:!1}));return{coin:xt(t),mats:t}}async function Wr(){let[e,t]=await Promise.all([vt(K),er()]);Dr=t,q.add(Dr.group);for(let t of e)t.transparent=!0;let n=xt(e),r=e;for(let e of r)Object.assign(e,{emissiveIntensity:0}).emissive.set(`#ff6a1a`);let i=new D(new A({map:Mr(),blending:2,transparent:!0,depthWrite:!1,fog:!1,opacity:0}));i.scale.set(1.1,2.6,1),q.add(i);let a=Array.from({length:or},()=>Ur(e)),o=Ur(e);o.coin.position.set(0,P/2,0),o.coin.visible=!1,q.add(n,o.coin,...a.map(e=>e.coin)),yr.hidden=!1,await K.compileAsync(Y.cosmos,Y.cosmosCamera),await K.compileAsync(q,J),Fr(`위로 튕겨서 던지기`);let s=performance.now(),c=oe(`dpr ${K.getPixelRatio()}`);K.setAnimationLoop(l=>{let u=Math.min(.05,(l-s)/1e3);s=l,c(l);let d=W.active,f=W.step(Q,u);!d&&W.active&&(zr.clear(),Br.clear(),Ir(null),Fr(`500m에 닿지 못했어요. 다시 던져 보세요`),vr.classList.remove(`is-shown`));for(let t of e)t.opacity=W.active?W.thrownOpacity:1,t.depthWrite=!W.active;o.coin.visible=W.active;for(let e of o.mats)e.opacity=W.waitingOpacity;for(let e of f)Rr(e);n.position.copy(Q.pos),n.quaternion.copy(Q.q);let p=Q.omega.length(),m=!W.active&&Q.phase===`air`?O(12,36,p)*.35:0;p>1e-6&&Vr.copy(Q.omega).divideScalar(p),a.forEach((e,t)=>{let n=(t+1)/4;if(e.coin.visible=m>.01,e.coin.visible){e.coin.position.copy(Q.pos),e.coin.quaternion.copy(Q.q).premultiply(Hr.setFromAxisAngle(Vr,-p*n/60));for(let t of e.mats)t.opacity=m*(1-n)}});let h=W.active?P/2+(Q.pos.y-P/2)*W.thrownOpacity:Q.pos.y,g=!W.active&&Q.phase===`air`?hr(h,Q.vel.y):0;for(let e of r)e.emissiveIntensity=g*1.2;i.visible=g>.01,i.position.set(Q.pos.x,Q.pos.y+1.1,Q.pos.z),i.material.opacity=g*(.85+.15*Math.sin(l/25)),$.setWind(Q.phase===`air`&&Lr.has(pr)&&Q.bounces===0&&Q.vel.y<0?Math.max(O(3,42,p),g)*mr(h):0);let _=f.some(e=>e.type===`ceiling`)?De:Math.min(De,we(h-P/2));if(!W.active&&Q.phase===`air`){vr.textContent=ke(_);for(let e of Ce)h<e.height||Lr.has(e.height)||(Lr.add(e.height),$.clink(.2,1.6))}document.body.classList.toggle(`is-night`,h>19),Z=Math.max(0,Z-u/.85),X.visible=Z>0,X.scale.setScalar(.55+2.4*(1-Z)),Er.opacity=.48*Z*Z;let v=1-O(3,9,h);Sr.opacity=v,Cr.visible=Tr.visible=v>0,Tr.scale.setScalar(N*2.8*(1+h*.15)),wr.opacity=(W.active?W.waitingOpacity:1)*v/(1+h*.6),Or.target=Math.max(0,h-rr),Or.value=C(Or.step(u),Or.target-ir,Or.target+ar);let y=O(F-10,F-4,h),b=Or.value+nr+y*1.1;kr.target=fr(h);let ee=kr.step(u);J.position.set(0,b+Math.sin(ee)*xr,Math.cos(ee)*xr),J.lookAt(0,b,0),J.updateMatrixWorld(),t.update(u,h,Q.vel.y,J),Y.update(h,l/1e3),br.color.copy(Y.horizon),K.clear(),K.render(Y.cosmos,Y.cosmosCamera),K.clearDepth(),K.render(q,J),yr.style.setProperty(`--fill`,String(Math.min(1,W.climb/Jn))),yr.classList.toggle(`is-done`,W.climb>=Jn)})}Wr().catch(e=>{console.error(e),Fr(`불러오지 못했어요. 새로고침해 주세요.`)});