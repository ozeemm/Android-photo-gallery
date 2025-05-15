using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace Server.Entities
{
    public class Photo
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }

        public string Name { get; set; }
        public string Date { get; set; }
        public string ImageString { get; set; }

        [ForeignKey("Album")]
        public int AlbumId { get; set; }
        
        [JsonIgnore]
        public virtual Album? Album { get; set; }

    }
}
