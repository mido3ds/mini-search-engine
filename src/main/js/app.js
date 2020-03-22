import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'

import { DefaultApi } from './api'

const api = new DefaultApi()

const Index = () => {
    const [content, setContent] = useState([])

    useEffect(() => {
        api.query("1").then(resp => {
            if (resp.status === 200) {
                console.log(resp.data);
                setContent(resp.data)
            } else {
                console.error(`error in query, resp.status={resp.status}`)
            }
        })
    }, [])

    return (
        <div>
            {
            content.map((c, i) =>
                <div key={i}>
                    <a href={c.link}>
                        <div>{c.title}</div>
                        {c.link}<br/>
                    </a>
                    <div>{c.snippet}</div><br/>
                </div>
            )
            }
        </div>
    )
}

render(<Index />, document.getElementById('app-root'))
