import React, {useState, useEffect} from 'react';
import {Loading} from '@alifd/next';
import mergeWith from 'lodash/mergeWith';
import isArray from 'lodash/isArray';
import {buildComponents, assetBundle, AssetLevel, AssetLoader} from '@alilc/lowcode-utils';
import ReactRenderer from '@alilc/lowcode-react-renderer';
import {injectComponents} from '@alilc/lowcode-plugin-inject';
import appHelper from '../../appHelper';
import {
  getProjectSchemaFromDb,
  getPackagesFromAssets
} from '../../services/schemaService';

const Renderer = (props) => {

  const {page} = props;

  console.log(page);

  const [data, setData] = useState({});

  useEffect(() => {
    setData({});
    init();
  }, [page])

  async function init() {
    const projectSchema = await getProjectSchemaFromDb(page);
    const packages = await getPackagesFromAssets();
    const {
      componentsMap: componentsMapArray,
      componentsTree,
      i18n,
      dataSource: projectDataSource,
    } = projectSchema;
    const componentsMap: any = {};
    componentsMapArray.forEach((component: any) => {
      componentsMap[component.componentName] = component;
    });
    const pageSchema = componentsTree[0];

    const libraryMap = {};
    const libraryAsset = [];
    packages.forEach(({package: _package, library, urls, renderUrls}) => {
      libraryMap[_package] = library;
      if (renderUrls) {
        libraryAsset.push(renderUrls);
      } else if (urls) {
        libraryAsset.push(urls);
      }
    });

    const vendors = [assetBundle(libraryAsset, AssetLevel.Library)];

    // TODO asset may cause pollution
    const assetLoader = new AssetLoader();
    await assetLoader.load(libraryAsset);
    const components = await injectComponents(buildComponents(libraryMap, componentsMap));

    setData({
      schema: pageSchema,
      components,
      i18n,
      projectDataSource,
    });

  }

  const {schema, components, i18n = {}, projectDataSource = {}} = data as any;

  function customizer(objValue: [], srcValue: []) {
    if (isArray(objValue)) {
      return objValue.concat(srcValue || []);
    }
  }

  return (
    <div className="lowcode-plugin-sample-preview" style={{ minHeight : '90vh' }}>
      {!schema ? <Loading style={{ width : '100%', height : '90vh' }}/> : <ReactRenderer
        className="lowcode-plugin-sample-preview-content"
        style={{ height : '100%' }}
        schema={{
          ...schema,
          dataSource: mergeWith(schema.dataSource, projectDataSource, customizer),
        }}
        components={components}
        messages={i18n}
        appHelper={appHelper}
      />
      }
    </div>
  );

};

export default Renderer;
