import Button from '@material-ui/core/Button'
import CircularProgress from '@material-ui/core/CircularProgress'
import TextField from '@material-ui/core/TextField'
import Autocomplete from '@material-ui/lab/Autocomplete'
import matchSorter from 'match-sorter'
import React, { useEffect, useState } from 'react'
import { DefaultApi } from './api'

const API = new DefaultApi()

const SearchBar = () => {
    const [disabled, setDisabled] = useState(true)
    const [query, setQuery] = useState("")
    const [open, setOpen] = useState(false)
    const [options, setOptions] = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        let active = true

        if (query !== "") {
            setLoading(true)
            API.complete(query).then(resp => {
                if (resp.status === 200 && active) {
                    setOptions(resp.data)
                }
            }).finally(() => {
                setLoading(false)
            })
        }

        return () => {
            active = false
        }
    }, [query])

    useEffect(() => {
        if (!open) {
            setOptions([])
        }
    }, [open])

    const onClick = () => {
        if (query) {
            window.location = `/search?q=${query}`
        }
    }

    const onKey = (e) => {
        if (e.key === 'Enter') {
            onClick()
            e.preventDefault()
        }
    }

    const onInputChange = (event) => {
        setQuery(event.target.value)
    }

    useEffect(() => {
        if (query === "") {
            setDisabled(true)
            setOptions([])
        } else if (disabled) {
            setDisabled(false)
        }
    }, [query])

    return (
        <div onKeyPress={onKey}>
            <h1>Mini Search Engine</h1>
            <form noValidate autoComplete="off" style={{ display: "flex" }}>
                <Autocomplete
                    id="autocomplete"
                    style={{ width: 300 }}
                    open={open}
                    onOpen={() => {
                        setOpen(true)
                    }}
                    onClose={(_, reason) => {
                        if (reason === "select-option") {
                            onClick()
                        }

                        setOpen(false)
                    }}
                    autoSelect={true}
                    getOptionSelected={(option, value) => option === value}
                    filterOptions={(options, { inputValue }) => matchSorter(options, inputValue)}
                    getOptionLabel={option => option}
                    options={options}
                    loading={loading}
                    freeSolo
                    renderInput={params => (
                        <TextField
                            {...params}
                            size="small"
                            id="search-input"
                            onChange={onInputChange}
                            variant="outlined"
                            InputProps={{
                                ...params.InputProps,
                                endAdornment: (
                                    <React.Fragment>
                                        {loading ? <CircularProgress color="inherit" size={20} /> : null}
                                        {params.InputProps.endAdornment}
                                    </React.Fragment>
                                ),
                            }}
                        />
                    )}
                />

                <Button variant="outlined" color="primary"
                    disableElevation onClick={onClick} disabled={disabled} style={{ marginLeft: "10px" }}>
                    Search
                </Button>
            </form>
            <br />
        </div>
    )
}

export default SearchBar;