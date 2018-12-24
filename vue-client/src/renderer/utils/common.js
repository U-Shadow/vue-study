const os = require('os')
const mysql = require('mysql')
const x2js = require('x2js')

exports.install = function (Vue, options) {
	Vue.prototype.bus = new Vue()			// new一个全局vue对象, 用于非父子组件之间的通信
	Vue.prototype.$newLine = os.EOL
	/**
	 * 消息提示
	 * @param {Object} msg 提示消息内容
	 * @param {Object} type 提示类型
	 */
  Vue.prototype.cusMessage = function(msg, type) {
  	this.$message({
      message: msg,
      type: type
    })
  }
  /**
   * 创建mysql连接
   * @param {Object} db_config
   */
  Vue.prototype.connectMysql = function(dbConfig) {
  	var connection = mysql.createConnection({
      host: dbConfig.host,
      port: dbConfig.port,
      user: dbConfig.userName,
      password: dbConfig.password,
      database: dbConfig.database
    })
    connection.connect()
    return connection
  }
  /**
   * 关闭mysql连接
   * @param {Object} connect
   */
  Vue.prototype.closeMysql = function(connect) {
  	connect.end((err) => {
        if(err){
          console.log("mysql关闭失败");
        }else{
        	console.log("mysql关闭成功");
        }
    });
  }
  Vue.prototype.$x2js = new x2js();
  Vue.prototype.$workspacePath = process.cwd() + '/workspace/'
}
