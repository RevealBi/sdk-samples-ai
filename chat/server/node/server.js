const reveal = require('reveal-sdk-node');
const revealAI = require('reveal-sdk-node-ai');
const express = require('express');
const cors = require('cors');
const path = require('path');
const os = require('os');

// ---------------------------------------------------------------------------
// AI provider settings (declarative "profiles" shape)
// Replace with your own API key or load from an environment variable.
// A named provider connection (openai) carries the credentials; a profile
// (gpt-5.6-luna) selects the model and is used as the default.
// ---------------------------------------------------------------------------
const OPENAI_API_KEY = process.env.OPENAI_API_KEY || 'YOUR_OPENAI_API_KEY';

const aiProviders = {
    openai: {
        type: 'OpenAI',
        apiKey: OPENAI_API_KEY
    }
};

const aiProfiles = {
    'gpt-5.6-luna': {
        provider: 'openai',
        model: 'gpt-5.6-luna'
    }
};

// ---------------------------------------------------------------------------
// Datasource provider – configures the SampleExcel web resource
// ---------------------------------------------------------------------------
const dataSourceProvider = async (userContext, dataSource) => {
    if (dataSource.id === 'SampleExcel' && dataSource instanceof reveal.RVWebResourceDataSource) {
        dataSource.useAnonymousAuthentication = true;
        dataSource.url = 'https://download.infragistics.com/reveal/sampledata/SamplesIA.xlsx';
    }
    return dataSource;
};

const dataSourceItemProvider = async (userContext, dataSourceItem) => {
    await dataSourceProvider(userContext, dataSourceItem.dataSource);
    return dataSourceItem;
};

// ---------------------------------------------------------------------------
// Reveal options
// ---------------------------------------------------------------------------
const revealOptions = {
    dataSourceProvider: dataSourceProvider,
    dataSourceItemProvider: dataSourceItemProvider,
    plugins: [
        revealAI.withOptions({
            defaultProfile: 'gpt-5.6-luna',
            providers: aiProviders,
            profiles: aiProfiles,
            metadataCatalogFile: path.resolve(__dirname, 'Reveal', 'Metadata', 'catalog.json'),
            metadataManager: {
                outputPath: path.resolve(os.homedir(), 'AImetadata'),
            }
        })
    ]
};

// ---------------------------------------------------------------------------
// Express app
// ---------------------------------------------------------------------------
const app = express();
app.use(cors());
app.use('/', reveal(revealOptions));

const PORT = parseInt(process.env.PORT || '5112', 10);
app.listen(PORT, () => {
    console.log(`Reveal AI Chat sample (Node) running on http://localhost:${PORT}`);
});
