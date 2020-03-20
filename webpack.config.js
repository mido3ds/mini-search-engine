const webpack = require("webpack");

module.exports = env => ({
  entry: './src/main/js/app.js',
  mode: 'development',
  devtool: 'source-map',
  output: {
    path: __dirname,
    filename: './src/main/resources/static/built/bundle.js'
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
