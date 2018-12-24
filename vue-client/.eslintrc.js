module.exports = {
  root: true,
  parser: 'babel-eslint',
  parserOptions: {
    sourceType: 'module'
  },
  env: {
    browser: true,
    node: true
  },
  extends: 'standard',
  globals: {
    __static: true
  },
  plugins: [
    'html'
  ],
  'rules': {
    // allow paren-less arrow functions
    'arrow-parens': 0,
    // allow async-await
    'generator-star-spacing': 0,
    // allow debugger during development
    'no-debugger': process.env.NODE_ENV === 'production' ? 2 : 0,
    // 不要使用制表符
    "no-tabs": 0,
    // 使用两个空格进行缩进
    "indent": 0,
    // 允许空格和 tab 的混合缩进
    "no-mixed-spaces-and-tabs": 0,
    // 禁止多余的冒号
    "no-extra-semi": 0,
    // 关闭分号的验证
    "semi": 0,
    // 关闭'//'后需要空格的验证
    "spaced-comment": 0,
    // 关键字后面加空格
    "keyword-spacing": 0,
    // 关闭字符串拼接操作符 (Infix operators) 之间要留空格
    "space-infix-ops": 0,
    // 关闭else 关键字要与花括号保持在同一行
    "brace-style": 0,
    // 关闭方法括号前面需要空格验证
    "space-before-function-paren": 0,
    "operator-linebreak": 0,
    "space-before-blocks": 0,
    "eqeqeq": 0,
    "quotes": 0,
    "camelcase": 0,
    "new-cap": 0
  }
}
