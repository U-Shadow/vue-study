<template>
  <div id="flow" class="flow">
  	<el-container>
		  <el-aside width="208px" v-show="isShowTree">
		  	<flow-tree @showMain="showMain"></flow-tree>
		  </el-aside>
		  <el-container>
		    <el-header>
		    	<flow-header @showMain="showMain"></flow-header>
		    </el-header>
		    <el-main>
		    	<flow-top-tool></flow-top-tool>
			    <flow-left-tool></flow-left-tool>
			    <flow-main-draw v-if="isShowMain" :isShowTree="isShowTree"></flow-main-draw>
		    </el-main>
		  </el-container>
		</el-container>
    <!--<flow-header  @showMain="showMain"></flow-header>
    <flow-top-tool></flow-top-tool>
    <flow-left-tool></flow-left-tool>
    <flow-main-draw v-if="isShowMain"></flow-main-draw>-->
    <!-- <flow-right></flow-right> -->
  </div>
</template>

<script>
import FlowHeader from './flow/FlowHeader'
import FlowTopTool from './flow/FlowTopTool'
import FlowLeftTool from './flow/FlowLeftTool'
import FlowMainDraw from './flow/FlowMainDraw'
import FlowTree from './flow/FlowTree'
// import FlowRight from './FlowRight'

export default {
  name: 'Flow',
  components: {
    FlowHeader,
    FlowTopTool,
    FlowLeftTool,
    FlowMainDraw,
    FlowTree
    // FlowRight,
  },
  data() {
  	return {
  		isShowMain: false,
  		isShowTree: true
  	}
  },
  methods: {
  	showMain(value) {
  		this.isShowMain = value
  	},
  	mainStyle() {
  		if(this.isShowTree) {
  			return "margin-left: 208px"
  		}
  		else {
  			return "margin-left: 0px"
  		}
  	}
  }
}
</script>

<style lang="scss">
@import './flow/css/flow.scss';
.el-header, .el-main{
	padding: 0px;
}
</style>
