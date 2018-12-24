<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form">
        <el-form-item label="数据源" :label-width="formLabelWidth">
      		<el-select v-model="form.data.datasourceTree" placeholder="" style="width: 100%;">
      		  <el-option label="请选择数据源" value=""></el-option>
      		  <el-option v-for="(item,index) in datasourceTree" :key="index"
      		  	:label="item.data.sourceName" :value="index"></el-option>
      		</el-select>
        </el-form-item>
        <el-form-item label="sql" :label-width="formLabelWidth">
			    <el-input v-model="form.data.sql" type="textarea" :autosize="{ minRows: 4, maxRows: 20}"></el-input>
			  </el-form-item>
      </el-form>
      
      <span slot="footer" class="dialog-footer">
	  		<el-button type="primary" @click="confirm">确 定</el-button>
		    <el-button @click="cancel">取 消</el-button>
		  </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { cloneDeep } from 'lodash/fp'
export default {
	name: 'jdbc',
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
		...mapState('flow', ['componentData', 'nodeData']),
		...mapState('processStore', ['datasourceTree'])
  },
	data () {
		return {
	  	formLabelWidth: '130px',
	  	form: {
	  		nodeType: 'jdbc',
	  		data: {// 实际业务数据
	  			datasource: '',
	  			sql: ''
	  		}
	   	}
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_COMPONENT_DATA']),
    //关闭
    doClose() {
      this.$emit('visibleChange', false);
    },
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
    	console.log(this.form)
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

<style scoped="scoped">
	.cus_footer{
		margin-top: 30px;
		text-align: center;
	}
	.jdbc_test {
		text-align:center
	}
</style>