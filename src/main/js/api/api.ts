import * as globalImportUrl from 'url';
import { Configuration } from './configuration';
import globalAxios, { AxiosPromise, AxiosInstance } from 'axios';
// Some imports not used depending on template conditions
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, RequestArgs, BaseAPI, RequiredError } from './base';

/**
 * 
 * @export
 * @interface QueryResult
 */
export interface QueryResult {
    /**
     * 
     * @type {string}
     * @memberof QueryResult
     */
    title?: string;
    /**
     * 
     * @type {string}
     * @memberof QueryResult
     */
    link?: string;
    /**
     * 
     * @type {string}
     * @memberof QueryResult
     */
    snippet?: string;
}

/**
 * DefaultApi - axios parameter creator
 * @export
 */
export const DefaultApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * 
         * @summary get list of completions
         * @param {string} q string to search for
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        complete(q: string, options: any = {}): RequestArgs {
            // verify required parameter 'q' is not null or undefined
            if (q === null || q === undefined) {
                throw new RequiredError('q','Required parameter q was null or undefined when calling complete.');
            }
            const localVarPath = `/complete`;
            const localVarUrlObj = globalImportUrl.parse(localVarPath, true);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }
            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            if (q !== undefined) {
                localVarQueryParameter['q'] = q;
            }


    
            localVarUrlObj.query = {...localVarUrlObj.query, ...localVarQueryParameter, ...options.query};
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            delete localVarUrlObj.search;
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...options.headers};

            return {
                url: globalImportUrl.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * 
         * @summary submit a query
         * @param {string} q string to search for
         * @param {number} [page] page of results to fetch, default 1
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        query(q: string, page?: number, options: any = {}): RequestArgs {
            // verify required parameter 'q' is not null or undefined
            if (q === null || q === undefined) {
                throw new RequiredError('q','Required parameter q was null or undefined when calling query.');
            }
            const localVarPath = `/query`;
            const localVarUrlObj = globalImportUrl.parse(localVarPath, true);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }
            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            if (q !== undefined) {
                localVarQueryParameter['q'] = q;
            }

            if (page !== undefined) {
                localVarQueryParameter['page'] = page;
            }


    
            localVarUrlObj.query = {...localVarUrlObj.query, ...localVarQueryParameter, ...options.query};
            // fix override query string Detail: https://stackoverflow.com/a/7517673/1077943
            delete localVarUrlObj.search;
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...options.headers};

            return {
                url: globalImportUrl.format(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * DefaultApi - functional programming interface
 * @export
 */
export const DefaultApiFp = function(configuration?: Configuration) {
    return {
        /**
         * 
         * @summary get list of completions
         * @param {string} q string to search for
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        complete(q: string, options?: any): (axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<string>> {
            const localVarAxiosArgs = DefaultApiAxiosParamCreator(configuration).complete(q, options);
            return (axios: AxiosInstance = globalAxios, basePath: string = BASE_PATH) => {
                const axiosRequestArgs = {...localVarAxiosArgs.options, url: basePath + localVarAxiosArgs.url};
                return axios.request(axiosRequestArgs);
            };
        },
        /**
         * 
         * @summary submit a query
         * @param {string} q string to search for
         * @param {number} [page] page of results to fetch, default 1
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        query(q: string, page?: number, options?: any): (axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<QueryResult>> {
            const localVarAxiosArgs = DefaultApiAxiosParamCreator(configuration).query(q, page, options);
            return (axios: AxiosInstance = globalAxios, basePath: string = BASE_PATH) => {
                const axiosRequestArgs = {...localVarAxiosArgs.options, url: basePath + localVarAxiosArgs.url};
                return axios.request(axiosRequestArgs);
            };
        },
    }
};

/**
 * DefaultApi - factory interface
 * @export
 */
export const DefaultApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    return {
        /**
         * 
         * @summary get list of completions
         * @param {string} q string to search for
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        complete(q: string, options?: any): AxiosPromise<Array<string>> {
            return DefaultApiFp(configuration).complete(q, options)(axios, basePath);
        },
        /**
         * 
         * @summary submit a query
         * @param {string} q string to search for
         * @param {number} [page] page of results to fetch, default 1
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        query(q: string, page?: number, options?: any): AxiosPromise<Array<QueryResult>> {
            return DefaultApiFp(configuration).query(q, page, options)(axios, basePath);
        },
    };
};

/**
 * DefaultApi - object-oriented interface
 * @export
 * @class DefaultApi
 * @extends {BaseAPI}
 */
export class DefaultApi extends BaseAPI {
    /**
     * 
     * @summary get list of completions
     * @param {string} q string to search for
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public complete(q: string, options?: any) {
        return DefaultApiFp(this.configuration).complete(q, options)(this.axios, this.basePath);
    }

    /**
     * 
     * @summary submit a query
     * @param {string} q string to search for
     * @param {number} [page] page of results to fetch, default 1
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof DefaultApi
     */
    public query(q: string, page?: number, options?: any) {
        return DefaultApiFp(this.configuration).query(q, page, options)(this.axios, this.basePath);
    }

}


