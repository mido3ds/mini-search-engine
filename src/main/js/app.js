import React from 'react';
import { render } from 'react-dom';

import HelloWorld from './helloworld';
import { DefaultApi } from './api';

let a = new DefaultApi();
console.log(a.query("a", 1));

render(<HelloWorld />, document.getElementById('app-root'));
