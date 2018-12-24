<template>
  <div>
    <el-dialog :title="componentDialogTitle" :visible="componentDialogVisible" @close="doClose" @open="doOpen" center >
      <el-form :model="form" ref="ruleForm">
      	<el-form-item label="数据源名称" :label-width="formLabelWidth" prop="data.sourceName" :rules="rules.sourceName">
        	<el-input v-model="form.data.sourceName"></el-input>
        </el-form-item>
        <el-form-item label="数据库类型" :label-width="formLabelWidth" prop="data.dbType" :rules="rules.dbType">
      		<el-select v-model="form.data.dbType" placeholder="" style="width: 100%;" @change="dbTypeChange">
      		  <el-option label="mysql" value="mysql"></el-option>
      		  <el-option label="Oracle" value="Oracle"></el-option>
      		  <el-option label="sqlserver" value="sqlserver"></el-option>
      		</el-select>
        </el-form-item>
        <el-form-item label="驱动类名称" :label-width="formLabelWidth">
        	<el-input v-model="form.data.driver" disabled></el-input>
        </el-form-item>
        <el-form-item label="连接地址" :label-width="formLabelWidth" prop="data.host" :rules="rules.host">
        	<el-input v-model="form.data.host"></el-input>
        </el-form-item>
        <el-form-item label="端口号" :label-width="formLabelWidth" prop="data.port" :rules="rules.port">
        	<el-input v-model="form.data.port" type="number"></el-input>
        </el-form-item>
        <el-form-item label="数据库" :label-width="formLabelWidth" prop="data.database" :rules="rules.database">
        	<el-input v-model="form.data.database"></el-input>
        </el-form-item>
      	<el-form-item label="用户名" :label-width="formLabelWidth" prop="data.userName" :rules="rules.userName">
        	<el-input v-model="form.data.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth" prop="data.password" :rules="rules.password">
        	<el-input v-model="form.data.password" type="password"></el-input>
        </el-form-item>
      	<el-form-item label="" class="jdbc_test">
			    <el-button type="primary" style="width: 80%; margin: 0 auto;" @click="connect">测试连接</el-button>
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
import mysql from 'mysql'
export default {
	name: 'dataSource',
	props: {
    componentDialogTitle: '',
    componentDialogVisible: Boolean,
		dialogActionType: {
    	type: Boolean
   	},
   	dataSourceData: {
 			type: Object
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
		var isValidIP = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入连接地址'));
      }
      else {
        var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
	    	if(!reg.test(value)) {
	    		return callback(new Error('请输入正确的连接地址'));
	    	}
        callback();
      }
    };
		return {
	  	formLabelWidth: '130px',
	  	form: {
	  		type: 'dataSource',
	  		prefixUrl: 'jdbc:mysql://',
	  		data: {// 实际业务数据
	  			sourceName: '',
	  			dbType: 'mysql',
	  			driver: 'com.mysql.jdbc.Driver',
	  			host: '',
	  			port: '',
	  			database: '',
	  			userName: '',
	  			password: '',
	  			dbUrl: ''
	  		}
	   	},
	  	rules: {
        sourceName: [
          { required: true, message: '请输入数据源名称', trigger: 'blur' }
        ],
        dbType: [
          { required: true, message: '请选择数据库类型', trigger: 'change' }
        ],
        host: [
          { required: true, validator: isValidIP, trigger: 'blur' }
        ],
        port: [
          { required: true, message: '请输入端口号', trigger: 'blur' }
        ],
        database: [
          { required: true, message: '请输入数据库名称', trigger: 'blur' }
        ],
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
		}
	},
	methods: {
		...mapMutations('processStore', ['UPDATE_DATASOURCE_TREE']),
		connect() {
			this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          var that = this
          var connection = mysql.createConnection({
            host: that.form.data.host,
            port: that.form.data.port,
            user: that.form.data.userName,
            password: that.form.data.password,
            database: that.form.data.database
          })
          connection.connect()
          connection.query('SELECT 1 + 1 AS solution', function (error, results, fields) {
            if (error) {
            	that.cusMessage('连接失败', 'warning')
            	throw error
            }
           	that.cusMessage('连接成功', 'success')
          })
        } else {
          console.log('error submit!!');
          return false;
        }
      });
		},
		dbTypeChange(value) {
			if(value === 'Oracle') {
				this.form.data.driver = 'oracle.jdbc.driver.OracleDriver'
			}
			else if(value === 'sqlserver') {
				this.form.data.driver = 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
			}
			else{
				this.form.data.driver = 'com.mysql.jdbc.Driver'
				this.form.prefixUrl = 'jdbc:mysql://'
			}
		},
    //关闭
    doClose() {
      this.$emit('doClose', false);
    },
    //打开
    doOpen() {
    	Object.assign(this.$data, this.$options.data())
    	// dialogActionType 为true表示新增数据源
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
      if(this.componentDialogVisible === true) {
        let data = this.datasourceTree[this.dataSourceData.id]
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
    	if(this.datasourceTree[this.form.data.sourceName]) {
  			this.cusMessage("该数据源名称已存在", "warning");
  			return;
  		}
    	this.$refs.ruleForm.validate((valid) => {
        if (valid) {
		    	this.form.data.dbUrl = this.form.prefixUrl + this.form.data.host + ':' + this.form.data.port + '/' + this.form.data.database
		    	let id = this.form.data.sourceName
		  		this.UPDATE_DATASOURCE_TREE({
		  			[id]: this.form
		  		})
		  		var result = {
		  			'visibleChange': false,
		  			'action': 'confirm',
		  			'datasourceId': this.form.data.sourceName
		  		}
		      this.$emit('visibleChange', result);
      	} else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    // 取消
    cancel() {
    	var result = {
  			'visibleChange': false,
  			'action': 'cancel'
  		}
    	this.$emit('visibleChange', result);
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