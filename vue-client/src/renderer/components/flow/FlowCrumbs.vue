<template>
  <div :class="isShowTree ? 'crumbs isShowTree' : 'crumbs'">
    <strong class="unuse">流程名称</strong>
    <span class="split-line">/</span>
    <strong>{{processInfo[currentProcess].processName}}</strong>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
	name: 'flowCrumbs',
	props: {
  	isShowTree: Boolean
  },
	computed: {
    ...mapState('flow', ['processInfo', 'currentProcess'])
 	}
}
</script>

<style  lang="scss">
.crumbs {
  width: 95%;
  height: 40px;
  position: fixed;
  top: 66px;
  left: 210px;
  z-index: 999;
  background: #fff;
  border-top: 1px solid #ddd;
  color: #333;
  padding-left: 20px;
  line-height: 40px;
  /*display: flex;*/
  align-items: center;
  .unuse {
    color: #999;
  }
  .split-line {
    margin: 0 8px;
  }
}
.isShowTree {
	margin-left: 208px;
}
</style>
