<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form">
      	<el-row style="margin-bottom: 5px;">
      		<el-button type="primary" size="mini" @click="addOutput"
      			style="float: right;">变量</el-button>
      	</el-row>
        <el-form-item label="输出内容" :label-width="formLabelWidth">
			    <el-input v-model="form.data.output" type="textarea" :autosize="{ minRows: 4, maxRows: 20}"></el-input>
			  </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirm">确 定</el-button>
		    <el-button @click="cancel">取 消</el-button>
		  </span>
    </el-dialog>
    
    <el-dialog title="选择变量或元素" class="endDialog" :visible="paramsOrEleVisible" width="500px" left>
    	<!--<ul>
    		<li v-for="(item, index) in processInfo[currentProcess].keys" :key="index">{{item}}</li>
    	</ul>-->
    	<div class="flt-container border-1">
	    	<ul>
	      	<li class="flt-tool-list">
	          <div class="flt-list-tit" @click.stop="processParamsShow = !processParamsShow">
	            <icon :name="processParamsShow?'down-arrow':'up-arrow'" :size="8" 
	            	class="flt-icon" style="vertical-align: 0em;"></icon>
	            <span>流程变量</span>
	          </div>
	          <div class="flt-shape-list" v-show="processParamsShow">
	            <div class="flt-item-box">
	            	<div :class="activeIndex === index ? 'li-list-item active ': 'li-list-item'" 
	            		v-for="(item,index) in processInfo[currentProcess].keys" :key="index"
	          			@click="selected(index)" @dblclick="confirmParamsOrEle(index)">
	          			<span style="margin-left: 10px;">{{item}}</span>
	              </div>
	            </div>
	          </div>
	        </li>
	     	</ul>
	    </div>
    	
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirmParamsOrEle(activeIndex)">确 定</el-button>
		    <el-button @click="cancelParamsOrEle">取 消</el-button>
		  </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { cloneDeep } from 'lodash/fp'
export default {
	name: 'end',
	props: {
    componentDialogTitle: '',
    componentDialogVisible: Boolean,
		dialogActionType: {
    	type: Boolean,
    	required: true
   	},
   	_nodeData: {
 			type: Object,
 			required: true
 		}
	},
	watch: {
    componentDialogVisible() {
		}
	},
	computed: {
		...mapState('flow', ['componentData', 'processInfo', 'currentProcess'])
  },
	data () {
		return {
			activeIndex: '',
			processParamsShow: false,
			paramsOrEleVisible: false,
	  	formLabelWidth: '130px',
	  	form: {
	  		nodeType: 'end',
	     	data: {
	     		output: ''
	     	}
	    }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA']),
		selected(index) {
			this.activeIndex = index
		},
		confirmParamsOrEle(index) {
			this.form.data.output += '{(' + this.processInfo[this.currentProcess].keys[index] + ')}'
			this.paramsOrEleVisible = false
		},
		cancelParamsOrEle() {
			this.paramsOrEleVisible = false
		},
    // 添加输出内容
    addOutput() {
  		this.paramsOrEleVisible = true
    },
    //关闭
    doClose() {
      this.$emit('visibleChange', false);
    },
    //打开
    doOpen() {
      this.setFormData()
    },
    setFormData() {
      if(this.componentDialogVisible === true) {
        let data = this.componentData[this._nodeData.id]
        if(data) {
          // 拷贝数据,避免再度修改数据的时候存在修改引用store的数据的错误
          this.form = cloneDeep(data)
        }
        else {
          Object.assign(this.$data, this.$options.data())
        }
      }
    },
    // 确定
    confirm() {
    	let id = this._nodeData.id
  		this.UPDATE_COMPONENT_DATA({
  			[id]: this.form
  		})
      this.$emit('visibleChange', false);
    },
    // 取消
    cancel() {
    	this.$emit('visibleChange', false);
    	Object.assign(this.$data, this.$options.data())
    }
	},
	mounted () {
    this.setFormData()
  }
}
</script>

<style lang="scss">
	.border-1{
		border: solid 1px #D3D3D3;
		min-height: 100px;
		padding: 5px 8px;
		.li-list-item{
			cursor: default;
		}
		.active{
			border: solid 1px #D3D3D3;
			border-radius:5px;
		}
	}
</style>