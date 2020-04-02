const webpack = require("webpack");
const path = require("path");

module.exports = env => ({
  mode: 'development',
  devtool: 'source-map',
  entry: {
    index: path.join(__dirname, 'src/main/js/pages/index/index.js'),
    search: path.join(__dirname, 'src/main/js/pages/search/index.js')
  },
  output: {
    path: path.join(__dirname, 'src/main/resources/static/built'),
    filename: '[name].js'
  },
  resolve: {
    extensions: ['.js', '.jsx', '.ts', '.tsx']
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: 'babel-loader'
      },
      { test: /\.tsx?$/, loader: "ts-loader" }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      "process.env.BASE_PATH": '"' + env.BASE_PATH + '"'
    })
  ]
});
