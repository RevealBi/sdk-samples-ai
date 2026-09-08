using Reveal.Sdk;
using Reveal.Sdk.AI;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers().AddReveal(builder =>
{
    builder.AddDataSourceProvider<RevealSdkServer.Reveal.DataSourceProvider>();
});
// Declarative "profiles" configuration built in code (consistent with the Node and Java
// samples): a named provider connection (openai) carries the credentials, and a profile
// (gpt-4.1) selects the model and is set as the default.
builder.Services.AddRevealAI()
    .AddOpenAI(openai => openai.ApiKey = builder.Configuration["RevealAI:OpenAI:ApiKey"])
    .AddProfile("gpt-4.1", profile =>
    {
        profile.Provider = "openai";
        profile.Model = "gpt-4.1";
    })
    .SetDefaultProfile("gpt-4.1")
    .UseMetadataCatalogFile("Reveal/Metadata/catalog.json");

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
      builder => builder.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()
    );
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
    app.UseCors("AllowAll");
}

app.UseAuthorization();

app.MapControllers();

app.Run();
