<template>
  <div>
    <el-dialog :title="queueDialogTitle" :visible="queueDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm">
      	<el-form-item label="队列名称" :label-width="formLabelWidth" prop="data.queueName" :rules="rules.queueName">
        	<el-input v-model="form.data.queueName"></el-input>
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
	name: 'dataSource',
	props: {
    queueDialogTitle: '',
    queueDialogVisible: Boolean,
		queueActionType: {
    	type: Boolean
   	},
   	queueData: {
 			type: Object
 		}
	},
	watch: {
    queueDialogVisible() {
		}
	},
	computed: {
		...mapState('flow', ['componentData', 'nodeData', 'queue'])
  },
	data () {
		return {
	  	formLabelWidth: '130px',
	  	form: {
	  		type: 'queue',
	  		data: {// 实际业务数据
	  			queueName: ''
	  		}
	   	},
	  	rules: {
        queueName: [
          { required: true, message: '请输入队列名称', trigger: 'blur' }
        ]
      }
		}
	},
	methods: {
		...mapMutations('flow', ['UPDATE_QUEUE']),
    //关闭
    doClose() {
      this.$emit('visibleChange2', false);
    },
    //打开
    doOpen() {
    	Object.assign(this.$data, this.$options.data())
    	// dialogActionType 为true表示新增队列
    	if(this.dialogActionType) {
    		this.$nextTick(() => {
        	this.$refs.ruleForm.resetFields();
        })
    	}
    	else {
    		this.setFormData()
    	}
    },
    setFormData() {
      if(this.queueDialogVisible === true) {
        let data = this.queue[this.queueData.id]
        if(data) {
        	// 拷贝数据,避免再度修改数据的时候存在修改引用store的数据的错误
          this.form = cloneDeep(data)
        }
        else {
    	  	Object.assign(this.$data, this.$options.data())
//        this.$nextTick(() => {
//        	this.$refs.ruleForm.resetFields();
//        })
        }
      }
    },
    // 确定
    confirm() {
    	this.$refs.ruleForm.validate((valid) => {
        if (valid) {
		    	let id = this.queueData.id
		  		this.UPDATE_QUEUE({
		  			[id]: this.form
		  		})
		      this.$emit('visibleChange2', false);
      	} else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    // 取消
    cancel() {
    	this.$emit('visibleChange2', false);
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