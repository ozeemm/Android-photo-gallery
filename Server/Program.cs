using Microsoft.EntityFrameworkCore;
using Server.Entities;
using System.Collections;
using System.Net;
using System.Text.Json.Serialization;

namespace Server
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddAuthorization();
            builder.Services.AddOpenApi();
            builder.Services.AddDbContext<AppContext>();
            builder.Services.AddCors();

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.MapOpenApi();
                app.UseSwaggerUI(options =>
                {
                    options.SwaggerEndpoint("/openapi/v1.json", "OpenAPI V1");
                });
            }

            app.UseCors(builder => builder.AllowAnyOrigin());

            app.UseHttpsRedirection();
            app.UseAuthorization();

            app.MapGet("/", () => new { Message = "Hello, World!" });

            // GET data
            app.MapGet("/api/data/photos", async (AppContext db) => await db.Photos.ToListAsync()).WithTags("Data");
            app.MapGet("/api/data/albums", async (AppContext db) => await db.Albums.ToListAsync()).WithTags("Data");
            app.MapGet("/api/data/backups", async (AppContext db) => await db.Backups.ToListAsync()).WithTags("Data");

            // POST data
            app.MapPost("/api/data/photos", async (AppContext db, Photo photo) =>
            {
                await db.Photos.AddAsync(photo);
                await db.SaveChangesAsync();

                return photo;
            }).WithTags("Data");
            app.MapPost("/api/data/albums", async (AppContext db, Album album) =>
            {
                await db.Albums.AddAsync(album);
                await db.SaveChangesAsync();

                return album;
            }).WithTags("Data");
            app.MapPost("/api/data/backups", async (AppContext db, Backup backup) =>
            {
                await db.Backups.AddAsync(backup);
                await db.SaveChangesAsync();

                return backup;
            }).WithTags("Data");

            // Mobile App Logic
            app.MapGet("/api/backups", async (AppContext db) => 
            {
                var backups = db.Backups.Select(b => new
                {
                    id = b.Id,
                    date = b.Date,
                    albumsCount = b.Albums.Count,
                    photosCount = b.Albums.SelectMany(a => a.Photos).Count()
                })
                .OrderByDescending(b => b.date);

                return backups;
            }).WithTags("Mobile app logic");
            app.MapGet("/api/backups/{id}", async (AppContext db, int id) =>
            {
                var backup = await db.Backups.Include(b => b.Albums)
                                        .ThenInclude(a => a.Photos)
                                        .Where(b => b.Id == id)
                                        .FirstOrDefaultAsync();

                if (backup == null)
                    return Results.BadRequest();

                return Results.Ok(backup);
            }).WithTags("Mobile app logic");
            app.MapPost("/api/backups", async (AppContext db, ICollection<Album> albums) =>
            {
                var backup = new Backup { Date = DateTime.Now };

                foreach (var album in albums)
                {
                    album.Backup = backup;
                }

                await db.Backups.AddAsync(backup);
                await db.Albums.AddRangeAsync(albums);
                await db.SaveChangesAsync();

                return backup;
            }).WithTags("Mobile app logic");
            app.MapDelete("/api/backups/{id}", async (AppContext db, int id) =>
            {
                var backup = await db.Backups.Where(b => b.Id == id).FirstOrDefaultAsync();
                if (backup == null)
                    return;

                db.Backups.Remove(backup);
                await db.SaveChangesAsync();
            }).WithTags("Mobile app logic");

            // Generators
            app.MapPost("/api/generate/test_data", async (AppContext db) => 
            {
                for (int n = 0; n < 2; n++)
                {
                    var backup = new Backup { Date = DateTime.Now };
                    var albums = new List<Album>();
                    var photos = new List<Photo>();

                    for (int i = 0; i < 2; i++)
                    {
                        var album = new Album { Name = $"Some album {i + 1}", Backup = backup };
                        albums.Add(album);

                        for (int j = 0; j < 3; j++)
                        {
                            var photo = new Photo { Name = $"Some photo {i + 1} {j + 1}", Date = DateTime.Now.ToString(), ImageString = "I'm string", Album = album };
                            photos.Add(photo);
                        }
                    }

                    await db.Backups.AddAsync(backup);
                    await db.Albums.AddRangeAsync(albums);
                    await db.Photos.AddRangeAsync(photos);

                    await db.SaveChangesAsync();
                }
            }).WithTags("Generators");
            app.MapDelete("/api/generate/delete_all", async (AppContext db) => 
            {
                db.Backups.RemoveRange(db.Backups);
                await db.SaveChangesAsync();
            }).WithTags("Generators");

            app.Run();
        }
    }
}
