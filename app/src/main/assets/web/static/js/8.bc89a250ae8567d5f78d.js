webpackJsonp([8],{IMP0:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=a("Xxa5"),n=a.n(s),i=a("exGp"),o=a.n(i),c=a("Dd8w"),l=a.n(c),r=a("NYxO"),p={name:"LingFaceAdjusting",data:function(){return{loading:!1,faceInfo:{person_path:"",temper_path:""},temp:{move_x:0,move_y:0,scale:1}}},computed:{getPerson:function(){return{left:this.faceInfo.loaction_x_1+"px",top:this.faceInfo.loaction_y_1+"px",width:this.faceInfo.loaction_x_3-this.faceInfo.loaction_x_1+"px",height:this.faceInfo.loaction_y_3-this.faceInfo.loaction_y_1+"px"}},getTemper:function(){return{left:this.faceInfo.loaction_x_1/4+this.temp.move_x+"px",top:this.faceInfo.loaction_y_1/4+this.temp.move_y+"px",width:(this.faceInfo.loaction_x_3-this.faceInfo.loaction_x_1)/4+"px",height:(this.faceInfo.loaction_y_3-this.faceInfo.loaction_y_1)/4+"px",transform:"scale("+this.temp.scale+")","-ms-transform":"scale("+this.temp.scale+")","-moz-transform":"scale("+this.temp.scale+")","-webkit-transform":"scale("+this.temp.scale+")","-o-transform":"scale("+this.temp.scale+")"}}},created:function(){this.genImage()},methods:l()({},Object(r.c)({photoPreview:"photoPreview",tempLocation:"tempLocation"}),{moveX:function(t){this.temp.move_x+=t},moveY:function(t){this.temp.move_y+=t},handleScale:function(t){this.temp.scale+=t},adjusting:function(){var t=this;return o()(n.a.mark(function e(){return n.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return t.loading=!0,e.prev=1,e.next=4,t.tempLocation(t.temp);case 4:e.next=8;break;case 6:e.prev=6,e.t0=e.catch(1);case 8:t.loading=!1;case 9:case"end":return e.stop()}},e,t,[[1,6]])}))()},genImage:function(){var t=this;return o()(n.a.mark(function e(){var a,s;return n.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return t.loading=!0,e.prev=1,e.next=4,t.photoPreview();case 4:a=e.sent,s=a.data,t.faceInfo=s,t.temp.move_x=+s.move_x,t.temp.move_y=+s.move_y,t.temp.scale=+s.scale,e.next=14;break;case 12:e.prev=12,e.t0=e.catch(1);case 14:t.loading=!1;case 15:case"end":return e.stop()}},e,t,[[1,12]])}))()}})},m={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-form",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"ling-face-adjusting",attrs:{"label-position":"right","label-width":"120px",model:t.faceInfo}},[a("p",{staticClass:"tips"},[t._v("将温度摄像头中的人脸框对准红外人脸的位置，方便获取温度数值时对应到相应的位置。")]),t._v(" "),a("el-form-item",{attrs:{label:"图片校准："}},[a("div",{staticClass:"images"},[a("div",{staticClass:"image"},[a("div",{staticClass:"temper-section"},[a("el-image",{staticClass:"temper",attrs:{src:t.faceInfo.temper_path}},[a("div",{staticClass:"image-slot",attrs:{slot:"placeholder"},slot:"placeholder"},[t._v("\n              加载中"),a("span",{staticClass:"dot"},[t._v("...")])])]),t._v(" "),a("div",{staticClass:"section",style:t.getTemper}),t._v(" "),a("div",{staticClass:"button"},[a("div",{staticClass:"arrow"},[a("i",{staticClass:"el-icon-caret-left",on:{click:function(e){return t.moveX(-1)}}}),t._v(" "),a("div",{staticClass:"arrow-center"},[a("i",{staticClass:"el-icon-caret-top",on:{click:function(e){return t.moveY(-1)}}}),t._v(" "),a("i",{staticClass:"el-icon-caret-bottom",on:{click:function(e){return t.moveY(1)}}})]),t._v(" "),a("i",{staticClass:"el-icon-caret-right",on:{click:function(e){return t.moveX(1)}}})]),t._v(" "),a("div",{staticClass:"scale"},[a("i",{staticClass:"el-icon-remove-outline",on:{click:function(e){return t.handleScale(-.1)}}}),t._v(" "),a("i",{staticClass:"el-icon-circle-plus-outline",on:{click:function(e){return t.handleScale(.1)}}})]),t._v(" "),a("el-button",{staticClass:"adjusting",attrs:{type:"primary"},on:{click:t.adjusting}},[t._v("校准")])],1)],1)]),t._v(" "),a("div",{staticClass:"image"},[a("div",{staticClass:"person-section"},[a("el-image",{staticClass:"person",attrs:{src:t.faceInfo.person_path}},[a("div",{staticClass:"image-slot",attrs:{slot:"placeholder"},slot:"placeholder"},[t._v("\n              加载中"),a("span",{staticClass:"dot"},[t._v("...")])])]),t._v(" "),a("div",{staticClass:"section",style:t.getPerson})],1)])])]),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:t.genImage}},[t._v("预览")])],1)],1)},staticRenderFns:[]};var f=a("VU/8")(p,m,!1,function(t){a("rZw4")},null,null);e.default=f.exports},rZw4:function(t,e){}});
//# sourceMappingURL=8.bc89a250ae8567d5f78d.js.map